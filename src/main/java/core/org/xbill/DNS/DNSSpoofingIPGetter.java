package org.xbill.DNS;

import packetproxy.gui.GUIOptionPrivateDNS;

public class DNSSpoofingIPGetter {
	private GUIOptionPrivateDNS gui;
	public DNSSpoofingIPGetter(GUIOptionPrivateDNS gui){
		this.gui = gui;
	}
	
	public boolean isAuto(){
		return gui.isAutoSpoofing();
	}
	
	public String get(){
		return gui.getSpoofingIP();
	}

	public String get6(){
		return gui.getSpoofingIP6();
	}

	public String getInt(){
		return gui.getBindInterface();
	}
}
