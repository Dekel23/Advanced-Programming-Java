## Rules for Configuration

To add a configuration, create a new file where all the agents will be defined\
Adding an agent require following these rules:

1. First line is the agent class chosen from all the agents in the program\
```graph.SortAgent graph.IndexAgent graph.Inc graph.Plus graph.BinOpAgent```

2. Second line is all the publishers, topics that the agent listen to

3. Third line is all the subscribers, topics that listen to the agent

Topic will be seperated by comma ```'```\
Avoid using spaces and empty lines as it will not interpreted