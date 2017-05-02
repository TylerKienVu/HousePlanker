package scripts;

import java.util.Vector;

import org.osbot.rs07.api.Bank;
import org.osbot.rs07.api.GrandExchange;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import mainFiles.Activity;

public class ItemGECheck implements Activity{
	private Script script;
	private String id = "itemGECheck";
	private Area GEArea = new Area(new Position(3171,3459,0),new Position(3152,3499,0));
	private Area bankArea = new Area(new Position(3161,3486,0),new Position(3168,3493,0));
	private Area castleWarsArea = new Area(new Position(2443,3082,0),new Position(2438,3097,0));
	private RS2Object portal;
	private Position[] positions = {new Position(3167,3490,0),new Position(3164,3487,0),
			new Position(3162,3489,0)};
	private Vector<buyItem> toBuy = new Vector<buyItem>();
	private Vector<sellItem> toSell = new Vector<sellItem>();
	private NPC bank;
	private NPC GE;
	private int randomVariable;
	private RS2Widget box1;
	private RS2Widget abort;
	private RS2Widget collect;


	public ItemGECheck(Script script){
		this.script = script;
	}
	
	@Override
	public String getID() {
		return id;
	}

	@Override
	public boolean validate() throws InterruptedException {
		portal = script.getObjects().closest(4525);
		return portal == null && GEArea.contains(script.myPosition());
	}

	@Override
	public void run() throws InterruptedException {
		if(!bankArea.contains(script.myPosition())){
				script.log("Walking to bank area");
				script.getWalking().webWalk(positions[Script.random(0,2)]);
		}
		checkGE();
		checkBank();
		useGE();
	}
	public class sellItem{
		private int id;
		private int quantity;
		private int price;
		public sellItem(int id, int price, int quantity){
			this.id = id;
			this.price = price;
			this.quantity = quantity;
		}
		public int getID(){
			return id;
		}
		public int getPrice(){
			return price;
		}
		public int getQuantity(){
			return quantity;
		}
	}
	
	public class buyItem{
		private String name;
		private int id;
		private int quantity;
		private int price;
		public buyItem(String name,int id,int price,int quantity){
			this.name = name;
			this.id = id;
			this.price = price;
			this.quantity = quantity;
		}
		public String getName(){
			return name;
		}
		public int getID(){
			return id;
		}
		public int getPrice(){
			return price;
		}
		public int getQuantity(){
			return quantity;
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
			rSleep(4000,10000);
		}
		else{
			rSleep(4000,7000);
		}
	}
	private void checkBank() throws InterruptedException {
		if(script.getGrandExchange().getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.EMPTY){
			if(toBuy.size() == 0 && toSell.size() == 0 && !script.getBank().isOpen()){
				openBank();
				moveOffScreen();
			}
			if(script.getBank().isOpen()){
				script.log("Checking bank");
				if(!script.getInventory().isEmpty()){
					script.getBank().depositAllExcept("Coins");
				}
				grabBankCoins();
				grabPlanks();
				grabROW();
				checkBankTabs();
				checkBankRings();
				checkBankLogs();
				moveOffScreen();
			}
		}
	}
	private void useGE() throws InterruptedException{
		if(toBuy.size() != 0 || toSell.size() != 0){
			if(!script.getGrandExchange().isOpen()){
				openGE();
			}
			if(toSell.size() != 0){
				script.log("Trying to sell");
				sellGE(toSell.get(0));
			}
			else if(toBuy.size() != 0){
				script.log("Trying to buy");
				buyGE(toBuy.get(0));
			}
		}
		else{
			script.log("Teleporting back");
			if(!script.getBank().isOpen()){
				openBank();
			}
			script.getBank().depositAllExcept("Coins");
			moveOffScreen();
			if(script.getBank().contains("Ring of dueling(8)")){
				script.getBank().withdraw("Ring of dueling(8)",1);
			}
			moveOffScreen();
			script.getBank().close();
			teleportToBank();
		}
	}
	private void checkGE() throws InterruptedException{
		openGE();
		moveOffScreen();
		if(script.getGrandExchange().getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.FINISHED_BUY ||
				script.getGrandExchange().getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.FINISHED_SALE ||
				script.getGrandExchange().getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.CANCELLING_BUY||
				script.getGrandExchange().getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.CANCELLING_SALE){
			script.log("Collecting");
			script.getGrandExchange().collect();
			moveOffScreen();
		}
		else if(script.getGrandExchange().getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.PENDING_BUY ||
				script.getGrandExchange().getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.PENDING_SALE ||
				script.getGrandExchange().getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.COMPLETING_BUY ||
				script.getGrandExchange().getStatus(GrandExchange.Box.BOX_1) == GrandExchange.Status.COMPLETING_SALE){
			abortOffer();
		}
	}
	private void openBank() throws InterruptedException{
		script.log("Opening bank");
		rSleep(1500,2000);
		bank = script.getNpcs().closest("Banker");
		if(bank != null){
			if(bank.interact("Bank")){
				new ConditionalSleep(5000){
					public boolean condition() throws InterruptedException{
						return script.getBank().isOpen();
					}
				}.sleep();
			}
			rSleep(1500,2000);
		}
	}
	private void grabBankCoins() throws InterruptedException{
		if(script.getBank().contains("Coins")){
			script.log("Grabbing Coins");
			moveOffScreen();
			script.getBank().withdrawAll("Coins");
		}
	}
	private void grabPlanks() throws InterruptedException{
		if(script.getBank().contains("Oak plank")){
			script.log("Adding to toSell: Oak plank");
			toSell.add(new sellItem(8779,500,(int)script.getBank().getAmount("Oak plank")));
			moveOffScreen();
			script.getBank().enableMode(Bank.BankMode.WITHDRAW_NOTE);
			moveOffScreen();
			script.getBank().withdrawAll("Oak plank");
			moveOffScreen();
			script.getBank().enableMode(Bank.BankMode.WITHDRAW_ITEM);
		}
	}
	private void grabROW() throws InterruptedException{
		if(script.getBank().contains("Ring of wealth")){
			script.log("Adding to toSell: Ring of wealth");
			script.log("Adding to toBuy: Ring of wealth (5)");
			toSell.add(new sellItem(2572,10000,1));
			toBuy.add(new buyItem("ring of wealth",11980,30000,1));
			script.getBank().withdraw("Ring of wealth", 1);
			moveOffScreen();
		}
		else if(script.getBank().getSlotForNameThatContains("Ring of wealth") == -1){
			script.log("Adding to toBuy: Ring of wealth (5)");
			toBuy.add(new buyItem("ring of wealth",11980,30000,1));
		}
	}
	private void checkBankTabs() throws InterruptedException{
		if(script.getBank().contains("Teleport to house")){
			int amount = (int) script.getBank().getAmount("Teleport to house");
			if(amount < 200){
				script.log("Adding to toBuy: Teleport to house");
				toBuy.add(new buyItem("teleport to house",8013, 1000,200-amount ));
			}
		}
		else{
			script.log("Adding to toBuy: Teleport to house");
			toBuy.add(new buyItem("teleport to house",8013, 1000,200));
		}
	}
	private void checkBankRings() throws InterruptedException{
		if(script.getBank().contains("Ring of dueling(8)")){
			int amount = (int) script.getBank().getAmount("Ring of dueling(8)");
			if(script.getBank().getAmount("Ring of dueling(8)") < 100){
				script.log("Adding to toBuy: Ring of dueling(8)");
				toBuy.add(new buyItem("ring of dueling",2552, 1300,100-amount ));
			}
		}
		else{
			script.log("Adding to toBuy: Ring of dueling(8)");
			toBuy.add(new buyItem("ring of dueling",2552, 1300,100));
		}
	}
	private void checkBankLogs() throws InterruptedException{
		if(script.getBank().contains("Oak logs")){
			int amount = (int) script.getBank().getAmount("Oak logs");
			if(amount < 4000){
				script.log("Adding to toBuy: Oak logs");
				toBuy.add(new buyItem("oak logs",1521, 120,4000-amount));
			}
		}
		else{
			script.log("Adding to toBuy: Oak logs");
			toBuy.add(new buyItem("oak logs",1521, 120,4000));
		}
	}
	private void openGE() throws InterruptedException{
		GE = script.getNpcs().closest("Grand Exchange Clerk");
		if(GE != null){
			if(!script.getGrandExchange().isOpen() && GE.interact("Exchange")){
				script.log("Opening GE");
				new ConditionalSleep(5000){
					public boolean condition() throws InterruptedException{
						return script.getGrandExchange().isOpen();
					}
				}.sleep();
			}
			rSleep(1500,2000);
		}
	}
	private void sellGE(sellItem item) throws InterruptedException{
		if(script.getGrandExchange().isOpen()){
			script.log("Selling: " + item.getID());
			script.getGrandExchange().sellItem(item.getID(), item.getPrice(), item.getQuantity());
			toSell.remove(0);
			moveOffScreen();
		}
	}
	private void buyGE(buyItem item) throws InterruptedException{
		if(script.getGrandExchange().isOpen()){
			script.log("Buying: " + item.getName());
			script.getGrandExchange().buyItem(item.getID(), item.getName(), item.getPrice(), item.getQuantity());
			toBuy.remove(0);
		}
	}
	private void teleportToBank() throws InterruptedException{
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
	private void abortOffer() throws InterruptedException{
		int newPrice = 0;
		int quantity = 0;
		int itemID = script.getGrandExchange().getItemId(GrandExchange.Box.BOX_1);
		String searchTerm = null;
		
		//handle widgets
		if(box1 == null){
			box1 = script.getWidgets().get(465, 7, 2);
		}
		box1.interact("View offer");
		Script.sleep(Script.random(1500,3000));
		if(abort == null){
			abort = script.getWidgets().get(465, 22,0);
		}
		if(collect == null){
			collect = script.getWidgets().get(465, 23,2);
		}
		//adjust for planks
		if(itemID == 8778){
			itemID = 8779;
		}
		
		//grab quantity
		quantity = script.getGrandExchange().getOfferQuantity();
		
		//handle aborting and adding new offer to queue
		if(script.getGrandExchange().isSellOfferOpen()){
			if(script.getGrandExchange().getOfferPrice() > 2000){
				newPrice = script.getGrandExchange().getOfferPrice() - 1000;
			}
			else{
				newPrice = script.getGrandExchange().getOfferPrice() - 100;
			}
			//place new offer
			if(newPrice != 0){
				script.log("Readjusting sell price");
				toSell.add(new sellItem(itemID,newPrice,quantity));
				abort.hover();
				rSleep(1500,2000);
				script.getMouse().click(false);
				rSleep(3000,5000);
				collect.hover();
				rSleep(1500,2000);
				script.getMouse().click(false);
				rSleep(3000,5000);
				script.getGrandExchange().goBack();
				rSleep(3000,5000);
			}
		}
		else if(script.getGrandExchange().isBuyOfferOpen()){
			if(script.getGrandExchange().getOfferPrice() > 1000){
				newPrice = script.getGrandExchange().getOfferPrice() + 1000;
			}
			else{
				newPrice = script.getGrandExchange().getOfferPrice() + 100;
			}
			if(newPrice != 0){
				if(itemID == 11980){
					searchTerm = "ring of wealth";
				}
				else if(itemID == 1521){
					searchTerm = "oak logs";
				}
				else if(itemID == 8013){
					searchTerm = "teleport to house";
				}
				else if(itemID == 2552){
					searchTerm = "ring of dueling";
				}
				if(searchTerm != null){
					script.log("Readjusting buy price");
					toBuy.add(new buyItem(searchTerm,itemID,newPrice,quantity));
					abort.hover();
					rSleep(1500,2000);
					script.getMouse().click(false);
					rSleep(3000,5000);
					collect.hover();
					rSleep(1500,2000);
					script.getMouse().click(false);
					rSleep(3000,5000);
					script.getGrandExchange().goBack();
					rSleep(3000,5000);
				}
			}
		}
	}
}
