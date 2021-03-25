![alt mpc_logo](https://raw.githubusercontent.com/laresistenciadelbit/MediaPcControl_server/main/readme_images/1.png)

### MediaPcControl (2019-2021) laresistenciadelbit

Official web link:

[https://wepaste.es/mediapccontrol/](https://wepaste.es/mediapccontrol/)


![alt mpc_app](https://raw.githubusercontent.com/laresistenciadelbit/MediaPcControl_server/main/readme_images/3.png)

In this picture we can see the android client of this server.

It main purpose is to control media controls in the computer from the smartphone.
Also in the main interface we can control some other keys like enter,space and also get and put the clipboard from/to the computer.


![alt mpc_server](https://raw.githubusercontent.com/laresistenciadelbit/MediaPcControl_server/main/readme_images/4_lite.png)

This is the server interface

.

---
---

### MORE INFO (SPANISH)

 - Parte del servidor basada en este ejemplo:  http://adblogcat.com/ssl-sockets-android-and-server-using-a-certificate/ (creo que ya no está online)

 - Clase de lectura/escritura del registro de windows que se usará para configurar el autoarranque (el mensaje es del propio autor, no encontré la fuente oficial) https://stackoverflow.com/questions/62289/read-write-to-windows-registry-using-java/1982033#1982033
 - Conversión de teclas a códigos de teclas para la clase Robot (https://stackoverflow.com/questions/1248510/convert-string-to-keyevents)
 - Controlmedia.dll para enviar teclas multimedia ya que java no permite mapear este tipo de teclas. Basado en este programa: https://batchloaf.wordpress.com/2012/04/17/simulating-a-keystroke-in-win32-c-or-c-using-sendinput/

    \\_ en realidad estoy simulando una dll como ejecutable, la forma correcta de hacerlo sería esta: https://stackoverflow.com/questions/30221022/how-to-emulate-pressing-media-keys-in-java/55321025#55321025 , https://github.com/nsnave/java-media-keys