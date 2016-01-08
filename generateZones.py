#!/usr/bin/env python

import sys, itertools, os, random

def randomContacts(roomExclude, dr):
    r = []
    for (room, computers) in dr:
        if room!=roomExclude:
            r += [random.choice(computers[:3])]
    return ",".join(r)

def main():
    content = sys.stdin.readlines()
    content = sorted(map(lambda x: x.strip(), content))
    rooms = itertools.groupby(content, lambda x: x[:-2])
    rooms = {x[0]: [y for y in x[1]] for x in rooms}
    total = open("selectedHosts", "wb")
    dr = filter(lambda (k,v): len(v)>3, rooms.iteritems())[:2]
    for (room, computers) in dr:
        for computer in computers[:3]:
            f = open('conf/'+computer, 'wb')
            f.write("contacts:"+"0>"+",".join(filter(lambda x: x!=computer, computers[:3]))+"#1>"+randomContacts(room, dr)+"\n")
            f.write("path:/uw/mimuw/"+room+"/"+computer+"\n")
            f.close()
            total.write(computer+"\n")
            os.system("cat settings.conf >> conf/"+computer)
    total.close()


if __name__ == '__main__':
    main()
