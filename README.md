# ALBA

This repository contains the source code of the ALBA mobile app, the counterpart of [ALBA-server](https://github.com/marsan27/Alba-Server/). This is the second in a series of projects I have been working on during my free time in order to improve my skills on software development, being the first one [Greta](https://github.com/marsan27/Greta/).
The ALBA app has been developed following the MVC-repository design pattern.

## What is ALBA?
ALBA is nothing but the product of one of the typical desires after watching an Iron Man film. Building an AI-powered system which controls and monitors your house, manages your appointments and, to put it bluntly, makes your life easier and cooler. (Let's forget about the flying combat suit for now).

Now obviously that is quite ~~crazy~~ an ambitious project and specially for a single developer, so I quickly decided to go for something simpler but without losing the spirit of the original idea (couldn't find any spare supercomputer at home to train the AI). Since nowadays I carry my phone almost wherever I go, I guessed that it could be a good replacement for voice inputs. For the control-your-house part, a conventional server running on a Raspberry PI and controlling some home peripherals would surely be enough. The AI part could be included later, once everything was running, using some external speech-to-text API. At the end of the day it did not seem that impossible, so I went for it.

Soon enough I realised this was going to take more than some days. Although I reserve the design details of the project, this is an overview of the main development phases I planned for this project before even considering it "finished":

## Features overview and roadmap

- [x] 1. Basic server implementation: Sockets, multithreading and multiprocessing decisions. MySQL database functionalities.
- [x] 2. Basic app programming: Design and decisions about UI and architecture. Sockets implementation. Connection with already developed server. Implementation of app database and architecture.
- [x] 3. Development of a custom protocol to use over the TCP network. Message parsers and builders. Ping-Pong functionalities to check for disconnections. Partition of long messages into smaller chunks. Design of JSON-formatted messages.
- [ ] 4. Advanced functionalities: TO-DO tasks storage and alarms. Server monitoring. User surveillance through the app. Just brainstorm ideas. __(Work in progress)__
- [ ] 5. Encryption of TCP packets, improvements on DB security (removing hardcoded passwords, ask for user input). Creation of installation script.
- [ ] 6. Support of external devices: IP cameras, alarms, etc. Coding of telegram bot for direct server communication. Try to integrate TTS and STT.
- [ ] *7. Security and pentesting phase. 

\* : The reason to leave this important phase for the end is that I still do not have the skills needed for this task. Until this is 'completed', no ports will be ever opened, the server will be kept LAN-only.

## Disclaimer
This is a personal side-project and by no means it is supposed to come with any warranty, it is offered AS-IS and I am not responsible of any harm derived from its use.

## License
This project is released under the GPL v3 license. See [LICENSE](https://github.com/marsan27/Alba/blob/master/LICENSE).
