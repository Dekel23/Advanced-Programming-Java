## Rules for Configuration

To add a configuration, create a new file where all the agents will be defined\
Adding an agent requires following these rules:

1. The first line is the agent class chosen from all the agents in the program\
```graph.SortAgent``` ```graph.IndexAgent``` ```graph.Inc``` ```graph.Plus``` ```graph.BinOpAgent```

2. The second line is all the publishers, topics that the agent listens to

3. The third line is all the subscribers, topics that listen to the agent

Topic will be separated by comma ```,```\
Avoid using spaces and empty lines as the file will not interpreted
