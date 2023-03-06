lamp_host("localhost").
lamp_port(8888).
detector_host("localhost").
detector_port(8890).
light_host("localhost").
light_port(8889).

lamp("off").
detection("false").
luminosity(0).

!start.

/* Plans */

+!start <-
    !createLampArtifact;
    !createDetectorArtifact;
    !createLightArtifact.

+!createLampArtifact   
    <-  ?lamp_host(H);
        ?lamp_port(P);
        makeArtifact(lamp, "acme.LampThingProxyArtifact", [H,P], L);
        focus(L);
        println("artifact created").

+state(S) <-
    println("new perceived lamp state: ", S);
    -lamp(_);
    +lamp(S).

+!createDetectorArtifact   
    <-  ?detector_host(H);
        ?detector_port(P);
        makeArtifact(detector, "acme.DetectorThingProxyArtifact", [H,P], L);
        focus(L);
        println("artifact created").

+detection(S) <-
    println("New perceived detector detection: ", S);
    -detect(_);
    +detect(S);
    !toggleLamp.

+!createLightArtifact   
    <-  ?light_host(H);
        ?light_port(P);
        makeArtifact(light, "acme.LightThingProxyArtifact", [H,P], L);
        focus(L);
        println("artifact created").

+luminosity(S) <-
    println("new perceived light luminosity: ", S);
    -lumino(_);
    +lumino(S);
    !toggleLamp.

+!toggleLamp <- 
    ?lamp(L);
    ?detect(D);
    ?lumino(U);
    if(D=="true") {
        if(U < "0.2") {
            if(L=="off") {
                on;
            }
        } else {
            if(L=="on") {
                off;
            }
        }
    } else {
        if(L == "on") {
            off;
        }
    }.

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$moiseJar/asl/org-obedient.asl") }
