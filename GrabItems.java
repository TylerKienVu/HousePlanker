package scripts;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import mainFiles.Activity;



public class GrabItems implements Activity{
	private Script script;
	private String id = "GrabItems";
	private Area castleWarsArea = new Area(new Position(2443,3082,0),new Position(2438,3097,0));
	private int randomVariable;
	
	public GrabItems(Script script){
		this.script = script;
	}
	
	@Override
	public String getID() {
		return id;
	}

	@Override
	public boolean validate() throws InterruptedException {
		return script.getBank().isOpen() &&
				castleWarsArea.contains(script.myPosition()) &&
				(!script.getInventory().contains("Coins","Teleport to house") ||
				script.getInventory().getSlotForNameThatContains("Ring of dueling") == -1 ||
				(script.getInventory().contains("Oak logs") && script.getInventory().getAmount("Oak logs") != 25) ||
				!script.getInventory().contains("Oak logs"));
	}

	@Override
	public void run() throws InterruptedException {
		if(script.getBank().isOpen()){
			//Grab Coins
			checkBankCoins();
			//Grab house tabs
			checkBankTabs();
			//Grab Ring of dueling
			checkBankRings();
			//Grab logs
			checkBankLogs();
			//Check inventory
			checkInventory();
			script.getBank().close();
		}
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
			rSleep(300,700);
		}
	}
	private void checkBankCoins() throws InterruptedException{
		if(script.getBank().isOpen() && 
			!script.getInventory().contains("Coins")){
			script.log("Grabbing Coins");
			moveOffScreen();
			if(script.getBank().contains("Coins") && script.getBank().getAmount("Coins") > Script.random(20000,30000)){
				script.getBank().withdrawAll("Coins");
			}
			else{
				script.log("Need to run to GE");
				teleportToGE();
			}
		}
	}
	private void checkBankTabs() throws InterruptedException{
		if(script.getBank().isOpen() &&
			!script.getInventory().contains("Teleport to house")){
			script.log("Grabbing house tabs");
			moveOffScreen();
			if(script.getBank().contains("Teleport to house") && script.getBank().getAmount("Teleport to house") > Script.random(2,7)){
				script.getBank().withdrawAll("Teleport to house");
			}
			else{
				script.log("Need to run to GE");
				teleportToGE();
			}
		}
	}
	private void checkBankRings() throws InterruptedException{
		if(script.getBank().isOpen() &&
			script.getInventory().getSlotForNameThatContains("Ring of dueling") == -1){
			script.log("Grabbing rings of dueling");
			moveOffScreen();
			if(script.getBank().contains("Ring of dueling(8)") && script.getBank().getAmount("Ring of dueling(8)") > Script.random(2,7)){
				script.getBank().withdraw("Ring of dueling(8)",1);
			}
			else{
				script.log("Need to run to GE");
				teleportToGE();
			}
		}
	}
	private void checkBankLogs() throws InterruptedException{
		if(script.getBank().isOpen() &&
				(script.getInventory().contains("Oak logs") && script.getInventory().getAmount("Oak logs") != 25) ||
				!script.getInventory().contains("Oak logs")){
			script.log("Grabbing oak logs");
			moveOffScreen();
			if(script.getBank().contains("Oak logs") && script.getBank().getAmount("Oak logs") >= 25){
				script.getBank().withdrawAll("Oak logs");
			}
			else{
				script.log("Need to run to GE");
				teleportToGE();
			}
		}
	}
	private void checkInventory() throws InterruptedException{
		if(script.getBank().isOpen() &&
				script.getInventory().contains("Coins") && script.getInventory().getAmount("Coins") < Script.random(20000,30000) ||
				script.getInventory().contains("Teleport to house") && script.getInventory().getAmount("Teleport to house") < Script.random(2,7)){
			script.log("Need to run to GE");
			teleportToGE();
		}
	}
	private void teleportToGE() throws InterruptedException{
		if(script.getBank().isOpen()){
			script.getBank().depositAll();
			int rowSlot = script.getBank().getSlotForNameThatContains("Ring of wealth");
			script.getBank().interact(rowSlot, "Withdraw-1");
			rSleep(1500,3000);
			script.getBank().close();
			rSleep(1500,3000);
		rowSlot = script.getInventory().getSlotForNameThatContains("Ring of wealth");
		if(script.getInventory().interact(rowSlot, "Rub")){
			new ConditionalSleep(5000){
				public boolean condition(){
					return script.getDialogues().inDialogue();
				}
			}.sleep();
		}
		rSleep(1500,3000);
		if(script.getDialogues().inDialogue())
			script.getDialogues().selectOption(2);
		}
	}
}

