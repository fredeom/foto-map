Project represents web application with servlet and vaadin api
to solve the problem of photos' storage and manipulation in cross link creation way.

Application consists of following part:
1. Server (servlet) - manipulates the graph of links between photos and storage
it in a sqlite database which will be accessed using SqlJet api.
It sends photos on demand from client using http protocol.
2. Client (Vaadin client) - manages interraction with multiple users allowing
them to add new photos, remove old and relink existing.

Project helps me to get familiar with new technologies and if prototype succeeded
allows to extend functionality of google/yandex street maps view for the people
without panoramic equipment cameras but with desire to make connected by means media.

I'll certainly use jdk, eclipse for fast development, maven for vaadin integration
and probably ant if it would be required to use legacy code.

***here i'll put links to the materials and code snippets i use***

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.