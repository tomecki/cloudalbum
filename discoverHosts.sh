#!/usr/bin/env python


import socket, os
for color in reversed(['red', 'cyan', 'violet', 'blue', 'yellow', 'khaki', 'green', 'pink', 'brown', 'orange']):
    for number in range(1, 16):
        hname = color+str(number).zfill(2)
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.settimeout(0.2)
        try:
            s.connect((hname, 22))
            print hname, socket.gethostbyname(hname)
            #os.system("host -4 "+hname+" | head -n 1 | cut -d' ' -f 4")
        except socket.error as e:
            pass
        finally:
            s.close()

