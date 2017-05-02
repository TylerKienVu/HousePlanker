package scripts;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import mainFiles.Activity;

public class TeleportToBank implements Activity{
	private Script script;
	private String id = "TeleportToBank";
	private Area castleWarsArea = new Area(new Position(2443,3082,0),new Position(2438,3097,0));
	private Area GEArea = new Area(new Position(3171,3459,0),new Position(3152,3499,0));

	
	public TeleportToBank(Script script){
		this.script = script;
	}
	
	@Override
	public String getID() {
		return id;
	}

	@Override
	public boolean validate() throws InterruptedException {
		RS2Object portal = script.getObjects().closest(4525);
		return (portal == null || !script.inventory.contains("Oak logs")) && 
				!castleWarsArea.contains(script.myPosition()) &&
				!GEArea.contains(script.myPosition());
	}

	@Override
	public void run() throws InterruptedException {
		int rodSlot = script.getInventory().getSlotForNameThatContains("Ring of dueling");
		if(rodSlot != -1){
			script.getInventory().hover(rodSlot);
			rSleep(1000,3000);
			if(script.getInventory().interact(rodSlot, "Rub")){
				new ConditionalSleep(Script.random(3000,7000)){
					@Override
					public boolean condition() throws InterruptedException{
						return script.getDialogues().inDialogue();
					}
				}.sleep();
			}
			rSleep(1000,3000);
			if(script.getDialogues().completeDialogue("Castle Wars Arena.")){
				new ConditionalSleep(Script.random(3000,7000)){
					@Override
					public boolean condition() throws InterruptedException{
						return castleWarsArea.contains(script.myPosition());
					}
				}.sleep();
			}
		}
	}
	private void rSleep(int x,int y) throws InterruptedException{
		Script.sleep(Script.random(x,y));
	}

}
