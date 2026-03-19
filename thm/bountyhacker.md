# bounty hacker

main page we are greeted with the bebop crew, nothing really on this page.

check the exifdata of the picture anyway, nothing interesting though

---

notable ports on the machine:

- 21
- 22
- 80

---

ftp allows anonymous access and we can get the note for the first question, which just includes some more cowboy bebop references, not sure if they will be important

there is however another file called locks.txt, with a bunch of password-looking lines, lets try to use them to brute force ssh with the name we found in the note

`hydra -l <name> -P locks.txt ssh://10.67.169.13`

which gets us this user's password!!

---

now that we have the user flag its all up to some privesc

- always check sudo -l
    - we have root access to tar, classic gtfobin

wrap it up folks

`sudo tar cf /dev/null /dev/null --checkpoint=1 --checkpoint-action=exec=/bin/sh`

and boom we're root baby

![bounty](/images/20260318_23h58m03s_grim.png)
