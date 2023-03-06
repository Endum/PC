# Activity-04
## Smart room with agents
### Idea
The project aim to simply the implementation of agents, in the other activity we saw how, even using ad hoc libraries for agents developing, there's a lot of overhead coming from the actual implementation, speaking of verbosity of language.
That fact move the attention from the actual logic implementation and delivering to code writing, in a sense slowing down the process of development and distract from the core of the idea.

### Jason
Jason is an interpreter for an extended version of AgentSpeak. It implements the operational semantics of that language, and provides a platform for the development of multi-agent systems, with many user-customisable features.
AgentSpeak is an agent-oriented programming language, based on logic programming and the desire-intention software model (BDI), architecture for autonomous agents.
Thanks to that language it is possible to implement agents and multi-agents based up only on the core functioning of what they have to realize, leaving out problematics like communication implementation (ip, connection establishing, packet loss...).
That abstraction is ported in Java thanks to jason, which makes possible to run an extended version of AgentSpeak directly in it, having all the good features of Java and the power of agents expression of AgentSpeak.

### Design
The idea is always the same, having a smart room with sensors able to turn on and off the light based on data collected, in this case sensor and actuator logic is written in the Jason part:
- ProxyArtifact: elements used by agents to communcate with other agents, asking for data, to take an action or subscribing to state changes as events.
- asl Jason: files where are described intention and logic of agents based on the AgentSpeak abstraction.
Anyway, the great idea to be able to program agents without having to deal with verbosity of java or communication stuff is not disappeared, but it is now detached away from the core logic of agents, that actually a good improvement: who works on communication doesn't really need to know how they're going to be used other than the abstraction needed and who is going to use those communication doesn't really need to know how they're gonna work behind the scenes.