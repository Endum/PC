# Activity-04
## Smart building with dt
### Idea
This last activity aim to exploit all the possibilities offered by JaCaMo, a framework for multi agent programming which is the combination of three separate technologies: Jason, Cartago and Moise; by implementing a smart building with multiple smart rooms.
We talked about Jason in the last activity, let's sum up with the other two.

### Cartago
CArtAgO is a general purpose framework that makes it possible to program and execute virtual environments for multi agents systems.
CArtAgO is based on the Agents & Artifacts (A&A) meta-model which introduces high-level metaphors taken from human cooperative working environments: agents as computational entities performing some kind of task/goal-oriented activity, and artifacts as resources and tools dynamically constructed, used, manipulated by agents to realise their individual and collective activities.
- Workspaces: A CArtAgO environment is given by one or multiple workspaces, in order to work inside a
workspace an agent must join it. By default, when booted, an agent is automatically joined to the
default workspace. Then, the same agent can join and work simultaneously in multiple workspaces.
- Agents’ action repertoire: By working inside a CArtAgO environment, the repertoire of an agent’s actions is determined by the set of artifacts available/usable in the workspace, in particular by the operations provided by such artifacts.

### Moise
Moise is an organisational model for Multi-Agent Systems based on notions like roles, groups, and missions. It enables an MAS to have an explicit specification of its organisation. This specification is to be used both by the agents to reason about their organisation and by an organisation platform that enforces that the agents follow the specification.

### Design
In order to follow an understandable and brief design, i'll talk about the three technologies separately, noticing how they actually work good together:
- Jason: the core functioning of smart room will be the same, an agent for every sensor, communicating to accomplish the expected behaviour. I'd add a building agent to aggregate smart rooms states, communicating with new room agents, which just mirror the state of each room (a sort of digital twin).
- Cartago: each room share the same group of artifacts, in this case used to communicate between agents, but they're not strictly the same, to have a logic separation we use workspaces, in particular one workspace for each room. Considering the new agents added (build agent and room agents), we'll need new artficats to realize communication with and within those.
- Moise: 