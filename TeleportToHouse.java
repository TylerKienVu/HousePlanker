package scripts;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import mainFiles.Activity;

public class TeleportToHouse implements Activity{
	private Script script;
	private String id = "TeleportToHouse";
	private Area castleWarsArea = new Area(new Position(2443,3082,0),new Position(2438,3097,0));
	private RS2Widget settings;
	private RS2Widget house;
	private RS2Widget servant;
	private int randomVariable;
	private RS2Object portal;
	
	public TeleportToHouse(Script script){
		this.script = script;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public boolean validate() throws InterruptedException {
		return castleWarsArea.contains(script.myPosition()) &&
				script.getInventory().contains("Coins") &&
				script.getInventory().contains("Teleport to house") &&
				script.getInventory().getSlotForNameThatContains("Ring of dueling") != -1 &&
				script.getInventory().contains("Oak logs") && script.getInventory().getAmount("Oak logs") == 25;
	}

	@Override
	public void run() throws InterruptedException {		
		if(script.getInventory().getAmount("Coins") < 20000){
			script.log("Not enough coins stopping script");
			script.stop();
		}
		script.getInventory().interact("Break","Teleport to house");
		if(settings == null){
			settings = script.getWidgets().get(548, 39);
		}
		if(house == null){
			house = script.getWidgets().get(261,76);
		}
		randomVariable = Script.random(1,3);
		if(randomVariable != 1){
			hoverMode();
		}
		else{
			moveOffScreen();
			portal = script.getObjects().closest(4525);
			if(portal == null){
				new ConditionalSleep(7000){
					public boolean condition() throws InterruptedException{
						portal = script.getObjects().closest(4525);
						return portal != null;
					}
				}.sleep();
			}
		}
	}
	private void rSleep(int x,int y) throws InterruptedException{
		Script.sleep(Script.random(x,y));
	}
	private void moveOffScreen() throws InterruptedException{
		randomVariable = Script.random(1,10);
		if(randomVariable==1){
			script.log("Moving mouse off screen");
			script.getMouse().moveOutsideScreen();
			rSleep(3000,9000);
		}
		else{
			rSleep(500,1500);
		}
	}
	private void hoverMode() throws InterruptedException{
		rSleep(500,1500);
		settings.interact("Options");
		rSleep(500,1000);
		if(house.hover()){
			new ConditionalSleep(7000){
				public boolean condition() throws InterruptedException{
					portal = script.getObjects().closest(4525);
					return portal != null;
				}
			}.sleep();
		}
		rSleep(1000,1500);
		house.interact("View House Options");
		rSleep(700,1500);
		if(servant == null){
			servant = script.getWidgets().get(370,15);
		}
		if(servant != null && servant.interact("Call Servant")){
			script.log("Call Servant Pressed (TeleportToHouse)");
		}
		else{
			script.log("Interaction failed (TeleportToHouse) trying again...");
			script.getMouse().move(Script.random(587,694), Script.random(415,430));
			script.getMouse().click(false);
		}
	}
}
