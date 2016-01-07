#!/usr/bin/env python

import sys, itertools, random

def main():
    content = sys.stdin.readlines()
    content = sorted(map(lambda x: x.strip(), content))
    rooms = itertools.groupby(content, lambda x: x[:-2])
    rooms = {x[0]: [y for y in x[1]] for x in rooms}
    print rooms
    for (room, computers) in filter(lambda (k,v): len(v)>3, rooms.iteritems())[:2]:
        for computer in computers[:3]:
            f = open('conf/'+computer, 'w')
            f.write(",".join(filter(lambda x: x!=computer, computers[:3])))
            f.close()


if __name__ == '__main__':
    main()
