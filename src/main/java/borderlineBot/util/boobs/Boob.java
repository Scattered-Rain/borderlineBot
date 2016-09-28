package borderlineBot.util.boobs;

public class Boob{
	
	
	private boolean touchable(){
		return false;
	}
	
	public void tryTouch(){
		if(touchable()){
			touch();
		}
		else{
			slap();
		}
	}
	
	private void touch(){
		
	}
	
	private void slap(){
		
	}
	
}
