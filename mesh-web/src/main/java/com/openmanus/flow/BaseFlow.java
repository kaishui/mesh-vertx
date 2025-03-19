package com.openmanus.flow;


import com.openmanus.agent.BaseAgent;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class BaseFlow {

  protected Map<String, BaseAgent> agents;
  protected Optional<BaseAgent> primaryAgent;
  protected Vertx vertx;

  public BaseFlow(Map<String, BaseAgent> agents, Vertx vertx) {
    this.agents = new HashMap<>();
    this.primaryAgent = Optional.empty();
    this.vertx = vertx;

    if (agents != null) {
      if (agents.values().stream().findFirst().isPresent()) {
        this.primaryAgent = agents.values().stream().findFirst();
      }
      this.agents.putAll(agents);
    }
  }

  public BaseFlow(BaseAgent agent, Vertx vertx) {
    this.agents = new HashMap<>();
    this.primaryAgent = Optional.of(agent);
    this.agents.put("primary", agent);
    this.vertx = vertx;
  }

  public BaseFlow(List<BaseAgent> agentList, Vertx vertx) {
    this.agents = new HashMap<>();
    this.primaryAgent = Optional.empty();
    this.vertx = vertx;
    if (agentList != null && !agentList.isEmpty()) {
      this.primaryAgent = Optional.of(agentList.get(0));
      for (int i = 0; i < agentList.size(); i++) {
        this.agents.put("agent_" + i, agentList.get(i));
      }
    }
  }

  public Map<String, BaseAgent> getAgents() {
    return agents;
  }

  public Optional<BaseAgent> getPrimaryAgent() {
    return primaryAgent;
  }

  public abstract Future<String> execute(String input);

  public void addAgent(String name, BaseAgent agent) {
    this.agents.put(name, agent);
    if (!this.primaryAgent.isPresent()) {
      this.primaryAgent = Optional.of(agent);
    }
  }

  public void addAgents(Map<String, BaseAgent> agents) {
    this.agents.putAll(agents);
    if (!this.primaryAgent.isPresent() && !agents.isEmpty()) {
      this.primaryAgent = agents.values().stream().findFirst();
    }
  }
}
