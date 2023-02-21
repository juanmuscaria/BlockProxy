package com.juanmuscaria.blockproxy.jna.enet.structures;

import com.juanmuscaria.blockproxy.jna.types.Size_t;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;
/**
 * <i>native declaration : ./enet/win32.h</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class ENetBuffer extends Structure {
	public Size_t dataLength;
	/** C type : void* */
	public Pointer data;
	public ENetBuffer() {
		super();
	}
	protected List<String> getFieldOrder() {
		return Arrays.asList("dataLength", "data");
	}
	/** @param data C type : void* */
	public ENetBuffer(Size_t dataLength, Pointer data) {
		super();
		this.dataLength = dataLength;
		this.data = data;
	}
	public ENetBuffer(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends ENetBuffer implements Structure.ByReference {
		
	}

    public static class ByValue extends ENetBuffer implements Structure.ByValue {
		
	}
}