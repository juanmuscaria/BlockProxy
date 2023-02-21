package com.juanmuscaria.blockproxy.jna.enet.structures;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;
/**
 * <i>native declaration : ./enet/list.h</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class ENetListNode extends Structure {
	/** C type : _ENetListNode* */
	public ByReference next;
	/** C type : _ENetListNode* */
	public ByReference previous;
	public ENetListNode() {
		super();
	}
	protected List<String> getFieldOrder() {
		return Arrays.asList("next", "previous");
	}
	/**
	 * @param next C type : _ENetListNode*<br>
	 * @param previous C type : _ENetListNode*
	 */
	public ENetListNode(ByReference next, ByReference previous) {
		super();
		this.next = next;
		this.previous = previous;
	}
	public ENetListNode(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends ENetListNode implements Structure.ByReference {
		
	}

	public static class ByValue extends ENetListNode implements Structure.ByValue {
		
	}
}
