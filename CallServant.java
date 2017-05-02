package scripts;


import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import mainFiles.Activity;

public class CallServant implements Activity{
	private Script script;
	private String id = "CallServant";
	private Area castleWarsArea = new Area(new Position(2443,3082,0),new Position(2438,3097,0));
	private RS2Widget settings;
	private RS2Widget house;
	private RS2Widget inventory;
	private RS2Widget inventoryIcon;
	private RS2Widget servant;
	private NPC dButler;
	private int randomVariable;
	private RS2Object portal;
	private int totalPlanksMade = 0;
	private int totalMoneySpent = 0;
	private int tabsUsed = 0;
	private int currentWorld = -1;
	
	public CallServant(Script script){
		this.script = script;
	}
	
	@Override
	public String getID() {
		return id;
	}

	@Override
	public boolean validate() throws InterruptedException {
		portal = script.getObjects().closest(4525);
		return !castleWarsArea.contains(script.myPosition()) && portal != null &&
				script.getInventory().isFull() && script.getInventory().contains("Oak logs");
	}

	@Override
	public void run() throws InterruptedException {
		if(currentWorld == -1){
			currentWorld = script.getWorlds().getCurrentWorld();
		}

		//grab widgets
		cacheWidgets();
		dButler = script.getNpcs().closest("Demon butler");
		
		//Make sure call servant button open
		if(dButler == null || !dButler.isInteracting(script.myPlayer())){
			callServant();
		}	
			
		//if butler taking too long
		if(!script.getDialogues().inDialogue()){
			if(dButler == null){
				dButler = script.getNpcs().closest("Demon butler");
			}
			moveOffScreen();
			
			if(dButler != null && dButler.exists() && !script.getDialogues().inDialogue() && dButler.interact("Talk-to")){
				new ConditionalSleep(7000){
					public boolean condition() throws InterruptedException{
						return script.getDialogues().inDialogue();
					}
				}.sleep();
			}
		}

		//Handle conversations
		script.log("Handling conversations");
		if(script.getDialogues().isPendingContinuation()){
			handleIntroDialogue();
		}
		//fast menu
		Script.sleep(Script.random(500,1500));
		if(script.getDialogues().selectOption("Take to sawmill: 25 x Oak logs")){
			fastDialogue();
		}
		else if(script.getDialogues().isPendingOption()){
			yesDialogue();
		}
		
		//default menu open
		else if(dButler != null && dButler.exists() && script.getInventory().contains("Oak logs")){
			defaultDialogue();
		}
			
		//teleport back
		if(!script.getDialogues().inDialogue() && !script.getInventory().contains("Oak logs")){
			teleportBack();
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
			rSleep(1000,1500);
		}
	}
	
	private void makeServantVisible() throws InterruptedException{
		if(house.isVisible()){
			house.interact("View House Options");
			rSleep(700,1500);
		}
		else if(inventory.isVisible()){
			settings.interact("Options");
			rSleep(500,1000);
			house.interact("View House Options");
			rSleep(700,1500);
		}
	}
	private void cacheWidgets() throws InterruptedException{
		if(settings == null){
			script.log("Caching settings");
			settings = script.getWidgets().get(548, 39);
		}
		if(house == null){
			script.log("Caching house");
			house = script.getWidgets().get(261, 76);
		}
		if(inventory == null){
			script.log("Caching inventory");
			inventory = script.getWidgets().get(548, 66);
		}
		if(inventoryIcon == null){
			script.log("Caching inventory icon");
			inventoryIcon = script.getWidgets().get(548, 55);
		}
	 }
	 private void callServant() throws InterruptedException{
		makeServantVisible();
		script.log("Calling Servant (CallServant)");
		if(dButler == null){
			dButler = script.getNpcs().closest("Demon butler");
		}
		makeServantVisible();
		servant = script.getWidgets().get(370,15);
		if(servant != null && servant.hover()){
			script.getMouse().click(false);
			rSleep(2000,3000);
		}
	 }
	 private void handleIntroDialogue() throws InterruptedException{
		script.log("Intro dialogue");
		rSleep(500,1000);
		script.getDialogues().clickContinue();
		rSleep(500,1000);
		
		//paying butler
		if(script.getDialogues().completeDialogue("Okay, here's 10,000 coins.")){
			rSleep(500,1000);
			script.getDialogues().clickContinue();
			totalMoneySpent += 10000;
		}
	 }
	 private void fastDialogue() throws InterruptedException{
	 	script.log("Fast dialogue");
		Script.sleep(Script.random(500,1000));
		if(script.getDialogues().clickContinue()){
			new ConditionalSleep(7000){
				public boolean condition() throws InterruptedException{
					return script.getDialogues().isPendingOption();
				}
			}.sleep();
		}
		rSleep(500,1000);
		yesDialogue();
	 }
	 private void yesDialogue() throws InterruptedException{
			if(script.getDialogues().selectOption(1)){
				new ConditionalSleep(7000){
					public boolean condition() throws InterruptedException{
						script.getDialogues().selectOption(1);
						return script.getDialogues().isPendingContinuation();
					}
				}.sleep();
			}
			rSleep(500,1000);
			script.getDialogues().clickContinue();
			rSleep(200,300);
			if(Script.random(1,3) == 1){
				script.log("Simulating space overclick");
				randomVariable = Script.random(1,3);
				for(int i = 0;i < randomVariable;i++){
					script.getKeyboard().typeString(" ",false);
					rSleep(100,300);
				}
			}
			rSleep(500,1000);
	 }
	 private void defaultDialogue() throws InterruptedException{
		script.log("Default dialogue");
		script.getInventory().getItem("Oak logs").interact("Use");
		Script.sleep(Script.random(1500,3000));
		if(dButler.interact("Use")){
			new ConditionalSleep(7000){
				public boolean condition() throws InterruptedException{
					return script.getDialogues().isPendingContinuation();
				}
			}.sleep();
		}
		rSleep(1500,3000);
		if(script.getDialogues().clickContinue()){
			new ConditionalSleep(7000){
				public boolean condition() throws InterruptedException{
					return script.getDialogues().isPendingOption();
				}
			}.sleep();
		}
		rSleep(1500,3000);
		script.getDialogues().selectOption(1);
		rSleep(2000,3000);
		script.getKeyboard().typeString("25", true);
		fastDialogue();
	 }
	 private void teleportBack() throws InterruptedException{
		script.log("Teleporting back");
		if(inventory.isHidden()){
			inventoryIcon.interact("Inventory");
			rSleep(300,1000);
		}
		int rodSlot = script.getInventory().getSlotForNameThatContains("Ring of dueling");
		randomVariable = Script.random(1,5);
		if(randomVariable == 1){
			script.getInventory().hover(rodSlot);
		}
		rSleep(300,1000);
		if(script.getInventory().interact(rodSlot, "Rub")){
			new ConditionalSleep(Script.random(3000,7000)){
				@Override
				public boolean condition() throws InterruptedException{
					return script.getDialogues().inDialogue();
				}
			}.sleep();
		}
		rSleep(300,1000);
		if(script.getDialogues().completeDialogue("Castle Wars Arena.")){
			totalPlanksMade += 25;
			totalMoneySpent += 6250;
			tabsUsed++;
			rSleep(200,300);
			if(Script.random(1,3) == 1){
				script.log("Simulating 2 overclick");
				randomVariable = Script.random(1,3);
				for(int i = 0; i < randomVariable; i++){
					script.getKeyboard().typeString("2",false);
					rSleep(100,300);
				}
			}
			new ConditionalSleep(Script.random(3000,7000)){
				@Override
				public boolean condition() throws InterruptedException{
					return castleWarsArea.contains(script.myPosition());
				}
			}.sleep();
		}
	 }
	 public int getTotalPlanksMade(){
		 return totalPlanksMade;
	 }
	 public int getTotalMoneySpent(){
		 return totalMoneySpent;
	 }
	 public int getTabsUsed(){
		 return tabsUsed;
	 }
	 public int getCurrentWorld(){
		 return currentWorld;
	 }
}
