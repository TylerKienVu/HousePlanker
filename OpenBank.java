package scripts;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import mainFiles.Activity;

public class OpenBank implements Activity{
	private Script script;
	private String id = "OpenBank";
	private Area castleWarsArea = new Area(new Position(2443,3082,0),new Position(2438,3097,0));
	private RS2Object bank;
	private int randomVariable;
	
	
	public OpenBank(Script script){
		this.script = script;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public boolean validate() throws InterruptedException {
		return !script.getBank().isOpen() && castleWarsArea.contains(script.myPosition()) &&
				(!script.getInventory().isFull() || !script.getInventory().onlyContains(
						"Coins","Teleport to house","Oak logs", "Ring of dueling(1)",
						"Ring of dueling(2)","Ring of dueling(3)","Ring of dueling(4)",
						"Ring of dueling(5)","Ring of dueling(6)","Ring of dueling(7)","Ring of dueling(8)"));
	}

	@Override
	public void run() throws InterruptedException {
		moveOffScreen();
		bank = script.getObjects().closest("Bank chest");
		randomVariable = Script.random(1,3);
		if(randomVariable != 1){
			script.getWalking().walk(new Position(2443,3083,0));
			moveOffScreen();
		}
		if(!script.getBank().isOpen()){
			openBank();
		}
		script.getBank().depositAllExcept("Coins","Teleport to house","Ring of dueling(1)",
				"Ring of dueling(2)","Ring of dueling(3)","Ring of dueling(4)",
				"Ring of dueling(5)","Ring of dueling(6)","Ring of dueling(7)","Ring of dueling(8)");
	}
	private void rSleep(int x,int y) throws InterruptedException{
		Script.sleep(Script.random(x,y));
	}
	private void moveOffScreen() throws InterruptedException{
		randomVariable = Script.random(1,20);
		if(randomVariable==1){
			script.log("Moving mouse off screen");
			script.getMouse().moveOutsideScreen();
			rSleep(3000,9000);
		}
		else{
			if(script.myPlayer().isMoving()){
				rSleep(1000,2000);
			}
			else{
				rSleep(300,500);
			}
		}
	}
	private void openBank() throws InterruptedException{
		randomVariable = Script.random(1,3);
		if(!script.getBank().isOpen() && bank != null){
			if(randomVariable == 1){
				if(bank.interact("Use")){
					script.log("Bank interact 1 executed");
					new ConditionalSleep(Script.random(3000,7000)){
						@Override
						public boolean condition() throws InterruptedException{
							return script.getBank().isOpen();
						}
					}.sleep();
				}
				moveOffScreen();
			}
			else if(Script.random(1,3) == 1){
				bank.hover();
				script.log("Hovering bank");
				rSleep(1000,2000);
			}
			else if(Script.random(1,10) == 1 && script.getInventory().contains("Coins")){
				script.log("Examining coins");
				script.getInventory().getItem("Coins").interact("Examine");
				rSleep(500,1000);
			}
			if(!script.getBank().isOpen() && bank.interact("Use")){
				script.log("Bank interact 2 executed");
				new ConditionalSleep(Script.random(3000,7000)){
					@Override
					public boolean condition() throws InterruptedException{
						return script.getBank().isOpen();
					}
				}.sleep();
			}
			moveOffScreen();
		}
	}
}
