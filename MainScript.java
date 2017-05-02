package mainFiles;

import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import scripts.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

//current goals: fix profit calc

@ScriptManifest(author = "Tylersobored", info = "Creates planks in POH", name = "HousePlanker", version = 0.6, logo = "")

public class MainScript extends Script {
	private ArrayList<Activity> activities = new ArrayList<Activity>();
	private TaskManager taskmanager;
	private long startTime;
	private String currentTask = null;
	private int totalPlanksMade = 0;
	private int totalMoneySpent = 0;
	private int tabsUsed = 0;
	private int currentWorld;
	
	@Override
	public void onStart() {
		Collections.addAll(activities,new ItemGECheck(this),new TeleportToBank(this),new OpenBank(this),new AutoMule(this),new GrabItems(this),new TeleportToHouse(this),new CallServant(this),new Wait(this));
		taskmanager = new TaskManager(activities,this);
		startTime = System.currentTimeMillis();
	}
	
	@Override
	public int onLoop() throws InterruptedException {
		Activity plankActivity = null;
		currentTask = taskmanager.getActive().getID();
		if(currentTask != null && currentTask == "CallServant"){
			plankActivity = taskmanager.getActive();
		}
		taskmanager.getActive().run();
		if(plankActivity != null){
			totalPlanksMade = ((CallServant) plankActivity).getTotalPlanksMade();
			totalMoneySpent = ((CallServant) plankActivity).getTotalMoneySpent();
			tabsUsed = ((CallServant) plankActivity).getTabsUsed();
			currentWorld = ((CallServant) plankActivity).getCurrentWorld();
		}
		return Script.random(500, 700);
	}
	
	@Override
	public void onExit() {
	}
	
	@Override
	public void onPaint(Graphics2D g) {
		g.setColor(Color.green);
		g.setFont(g.getFont().deriveFont(14.0f));
		g.drawString("World: " + currentWorld, 18, 230);
		g.drawString("Planks: " + totalPlanksMade, 18, 270);
		g.drawString("Money Spent: " + totalMoneySpent, 18, 290);
		g.drawString("Current task: " + currentTask, 18, 310);
		g.drawString("Profit: " + ((totalPlanksMade * 500) - totalMoneySpent - (tabsUsed * 800) - (totalPlanksMade * 100)), 18, 250);
		g.drawString(formatTime(System.currentTimeMillis() - startTime), 18, 330);
	}
	public final String formatTime(final long ms){
	    long s = ms / 1000, m = s / 60, h = m / 60;
	    s %= 60; m %= 60; h %= 24;
	    return String.format("%02d:%02d:%02d", h, m, s);
	}
}