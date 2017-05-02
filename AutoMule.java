package scripts;

import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import mainFiles.Activity;

public class AutoMule implements Activity{
	private Script script;
	private String id = "AutoMule";
	private String mule = "Suprix";
	private java.util.List<Player> listOfPlayers;
	private Player player = null;
	
	public AutoMule(Script script){
		this.script = script;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public boolean validate() throws InterruptedException {
		if(player == null){
			this.listOfPlayers = script.getPlayers().getAll();
			for(Player p : listOfPlayers){
				if(p.getName().equalsIgnoreCase(mule)){
					player = p;
					script.log("Found Mule: " + p.getName());
				}
			}
		}
		if(player != null && player.exists()){
			//check value of bank to see if worth trading
			if(script.getBank().isOpen()){
				if(script.getBank().contains("Coins")){
					script.getBank().withdrawAll("Coins");
				}
				if(script.getBank().contains("Oak plank")){
					if(script.getBank().getAmount("Oak plank") * 420 +
							script.getInventory().getAmount("Coins") > 1500000){
						return true;
					}
				}
				else if(script.getInventory().contains("Coins") &&
						script.getInventory().getAmount("Coins") > 1500000){
					return true;
				}
			}
			//if trying to trade already
			else if(!script.getBank().isOpen() && script.getInventory().contains("Coins") &&
					!script.getInventory().contains("Teleport to house")){
				return true;
			}
		}
		return false;
	}

	@Override
	public void run() throws InterruptedException {
		checkMule();
	}
	private void checkMule() throws InterruptedException{
		if(script.getBank().isOpen() && player != null && player.exists()){
			if(script.getBank().contains("Oak plank")){
				//Teleport to GE to liquify
				teleportToGE();
				rSleep(1000,3000);
			}
			else{
				tradeMule();
				rSleep(1000,3000);
			}
		}
		//Incase if already trying to trade
		else if(player != null && player.exists() && script.getInventory().onlyContains("Coins")){
			tradeMule();
		}
	}
	private void rSleep(int x,int y) throws InterruptedException{
		Script.sleep(Script.random(x,y));
	}
	private void tradeMule() throws InterruptedException{
		
		if(script.getBank().isOpen()){
			script.getBank().depositAll();
			rSleep(1000,3000);
		}
		//Grab coins if any in bank
		if(script.getBank().isOpen() && script.getBank().contains("Coins")){
			script.getBank().withdrawAll("Coins");
			rSleep(1000,3000);
			script.getBank().deposit("Coins", 1000000);
			rSleep(1000,3000);
			script.getBank().close();
			rSleep(1000,3000);
		}
		//Initiate trade with mule
		if(player != null && player.exists() && script.getInventory().onlyContains("Coins")){
			script.log("Handling trade");
			handleTrade();
		}
	}
	private void handleTrade() throws InterruptedException{
		this.listOfPlayers = script.getPlayers().getAll();
		for(Player p : listOfPlayers){
			if(p.getName().equalsIgnoreCase(mule)){
				player = p;
				script.log("Found Mule: " + p.getName());
			}
		}
		//If not in trade initiate trade
		if(!script.getTrade().isCurrentlyTrading()){
			if(player.interact("Trade with")){
				new ConditionalSleep(15000){
					@Override
					public boolean condition() throws InterruptedException {
						return script.getTrade().isCurrentlyTrading();
					}
				}.sleep();
			}
		}
		rSleep(1500,3000);
		
		//Handle the trade windows
		handleFirstWindow();
		handleSecondWindow();
	}
	private void handleSecondWindow() throws InterruptedException{
		if(script.getTrade().isCurrentlyTrading() && script.getTrade().isSecondInterfaceOpen()){
			if(this.script.trade.acceptTrade()){
				new ConditionalSleep(15000){
					@Override
					public boolean condition() throws InterruptedException {
						return !script.trade.isCurrentlyTrading();
					}
				}.sleep();
			}
			rSleep(1500,3000);
		}
	}
	private void handleFirstWindow() throws InterruptedException{
		if(script.getTrade().isCurrentlyTrading() && script.getTrade().isFirstInterfaceOpen()){
			if(script.getInventory().interact("Offer-All","Coins")){
				new ConditionalSleep(15000){
					@Override
					public boolean condition() throws InterruptedException {
						return script.getTrade().getOurOffers().contains("Coins");
					}
				}.sleep();
			}
			rSleep(1500,3000);
			if(script.getTrade().acceptTrade()){
				new ConditionalSleep(15000){
					@Override
					public boolean condition() throws InterruptedException {
						return script.trade.isSecondInterfaceOpen();
					}
				}.sleep();
			}
			rSleep(1500,3000);
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
