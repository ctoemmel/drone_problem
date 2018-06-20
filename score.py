"""
Scoring/validation tool for HashCode 2016 qualification task.
"""
from __future__ import print_function, division

import sys
from abc import ABCMeta
from abc import abstractmethod
from math import sqrt
from math import ceil
from functools import partial
from collections import defaultdict
from collections import Counter


class Actor(object):
    __metaclass__ = ABCMeta

    def __init__(self, index, location):
        self.index = index
        self.location = tuple(location)
        if len(self.location) != 2:
            raise ValueError('%r is not a valid location'
                             % ' '.join(self.location))

    def distance(self, other):
        ra, ca = self.location
        rb, cb = other.location
        return sqrt((ra-rb)**2 + (ca-cb)**2)

    @abstractmethod
    def get(self, product, number):
        pass

    @abstractmethod
    def put(self, product, number):
        pass


class Warehouse(Actor):
    def __init__(self, index, location, products):
        Actor.__init__(self, index, location)
        self.products = list(products)

    def get(self, product, number):
        assert self.products[product] >= number, \
            'cannot load %d of product %d, only %d left' \
            % (number, product, self.products[product])
        self.products[product] -= number

    def put(self, product, number):
        self.products[product] += number


class Order(Actor):
    # second parameter is ignored to make loading from file easier
    def __init__(self, index, location, _, products):
        Actor.__init__(self, index, location)
        self.products = Counter(products)
        self.last_delivered = None

    def get(self, product, number):
        raise NotImplementedError('cannot remove items from Orders')

    def put(self, product, number):
        assert self.products[product] >= number, \
            'cannot deliver %d of product %d, only %d needed' \
            % (number, product, self.products[product])
        self.products[product] -= number

    def done(self):
        return sum(self.products.values()) is 0


class Drone(Actor):
    def __init__(self, index, location, capacity, product_weights):
        Actor.__init__(self, index, location)
        self.location = tuple(location)
        self.remaining_capacity = capacity
        self.product_weights = product_weights
        self.cargo = defaultdict(lambda: 0)

    def put(self, product, number):
        total_weight = self.product_weights[product] * number
        assert self.remaining_capacity >= total_weight, \
            'cannot load %d of product %d, need %d capacity but only %d left' \
            % (number, product, total_weight, self.remaining_capacity)
        self.remaining_capacity -= total_weight
        self.cargo[product] += number

    def get(self, product, number):
        assert self.cargo[product] >= number, \
            'cannot unload %d of product %d, only %d on drone' \
            % (number, product, self.cargo[product])
        total_weight = self.product_weights[product] * number
        self.remaining_capacity += total_weight
        self.cargo[product] -= number


class Command(object):
    __metaclass__ = ABCMeta

    def __init__(self, drone):
        self.drone = drone

    @abstractmethod
    def turns_remaining(self):
        pass

    @abstractmethod
    def perform(self, turn):
        pass


class WaitCommand(Command):
    def __init__(self, drone, turns):
        Command.__init__(self, drone)
        self.__turns = turns

    def __repr__(self):
        return '<Command %d W %d>' % (self.drone, self.__turns)

    def turns_remaining(self):
        return self.__turns

    def perform(self, turn):
        pass


class TransferCommand(Command):
    def __init__(self, drone, source, target, product, number):
        Command.__init__(self, drone)
        self.source = source
        self.target = target
        self.product = product
        self.number = number

    def __repr__(self):
        if isinstance(self.target, Drone):
            target = self.source.index
            char = 'L'
        else:
            target = self.target.index
            if isinstance(target, Warehouse):
                char = 'U'
            else:
                char = 'D'
        return '<Command %d %s %d %d %d>' \
               % (self.drone, char, target, self.product, self.number)

    def turns_remaining(self):
        return ceil(self.source.distance(self.target)) + 1

    def perform(self, turn):
        s = self.source
        t = self.target
        if isinstance(s, Drone):
            s.location = t.location
        else:
            t.location = s.location
        if isinstance(t, Order):
            t.last_delivered = turn
        p = self.product
        n = self.number
        s.get(p, n)
        t.put(p, n)


def int_split(f):
    return map(int, next(f).split())


def parse_input(path):
    with open(path) as f:
        p = partial(int_split, f)
        nrows, ncolumns, ndrones, max_turns, payload = p()
        p()
        weights = list(p())
        nwarehouses, = p()
        warehouses = [Warehouse(i, p(), p()) for i in range(nwarehouses)]
        norders, = p()
        orders = [Order(i, p(), p(), p()) for i in range(norders)]
    drones = [Drone(i, warehouses[0].location, payload, weights)
              for i in range(ndrones)]
    return max_turns, drones, warehouses, orders


def parse_result(problempath, resultpath):
    max_turns, drones, warehouses, orders = parse_input(problempath)
    commands = [[] for _ in range(len(drones))]
    with open(resultpath) as f:
        ncommands = int(next(f))
        for _ in range(ncommands):
            parts = next(f).split()
            d = int(parts[0])
            c = parts[1]
            args = map(int, parts[2:])
            if c == 'W':
                command = WaitCommand(d, *args)
            else:
                t, p, n = args
                if c == 'U':
                    command = TransferCommand(d, drones[d], warehouses[t], p, n)
                elif c == 'L':
                    command = TransferCommand(d, warehouses[t], drones[d], p, n)
                elif c == 'D':
                    command = TransferCommand(d, drones[d], orders[t], p, n)
                else:
                    raise ValueError('unknown command %r' % c)
            commands[d].append(command)
    return max_turns, commands, orders


def simulate(commands, max_turns):
    schedule = defaultdict(lambda: [])
    # add first command for all drones
    for drone in commands:
        schedule[drone[0].turns_remaining()].append(drone)
    turn = 0
    command = None
    # while there are scheduled commands
    while schedule and turn < max_turns:
        try:
            for drone in schedule[turn]:
                command = drone.pop(0)
                command.perform(turn)
                try:
                    schedule[turn + drone[0].turns_remaining()].append(drone)
                # no more commands for this drone
                except IndexError:
                    pass
            schedule.pop(turn)
        # nothing was scheduled for this turn
        except KeyError:
            pass
        except AssertionError as e:
            print('Turn', turn, command, *e.args)
            sys.exit(1)
        turn += 1
    return schedule


def score(problempath, resultpath):
    max_turns, commands, orders = parse_result(problempath, resultpath)
    schedule = simulate(commands, max_turns)
    if schedule:
        print('Commands end after turn limit T=%d' % max_turns)
        for drone in schedule.values():
            for command in drone:
                print(command)
        sys.exit(1)
    return sum([
        ceil((max_turns - o.last_delivered) / max_turns * 100) for o in orders
        if o.done()
    ])


def main():
    import argparse
    parser = argparse.ArgumentParser(
        description=__doc__,
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    parser.add_argument(
        'inputfile',
        type=str,
        help='problem definition file',
    )
    parser.add_argument(
        'resultfile',
        type=str,
        help='result/command file',
    )
    args, unknown = parser.parse_known_args()
    print(score(args.inputfile, args.resultfile))


if __name__ == '__main__':
    main()
