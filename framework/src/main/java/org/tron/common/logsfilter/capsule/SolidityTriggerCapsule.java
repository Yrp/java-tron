package org.tron.common.logsfilter.capsule;

import lombok.Getter;
import lombok.Setter;
import org.tron.common.logsfilter.EventPluginLoader;
import org.tron.common.logsfilter.trigger.SolidityTrigger;
import org.tron.common.logsfilter.trigger.Trigger;

public class SolidityTriggerCapsule extends TriggerCapsule {

  @Getter
  @Setter
  private SolidityTrigger solidityTrigger;

  public SolidityTriggerCapsule(long latestSolidifiedBlockNum) {
    solidityTrigger = new SolidityTrigger();
    solidityTrigger.setLatestSolidifiedBlockNumber(latestSolidifiedBlockNum);
  }

  public void setTimeStamp(long timeStamp) {
    solidityTrigger.setTimeStamp(timeStamp);
  }

  @Override
  public void processTrigger() {
    EventPluginLoader.getInstance().postSolidityTrigger(solidityTrigger);
  }

  @Override
  public Trigger getTrigger() {
    return solidityTrigger;
  }
}

