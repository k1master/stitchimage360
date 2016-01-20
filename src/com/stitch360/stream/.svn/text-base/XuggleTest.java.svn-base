package com.stitch360.stream;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IPixelFormat;

public class XuggleTest {

	
	public static void main(String args[]){
		
		//change this
		String outFileUrl = "example.flv";
		
		//IContainer object-represent the file to which we want to encode
		IContainer outContainer = IContainer.make();

		int retval = outContainer.open(outFileUrl, IContainer.Type.WRITE, null);
		if (retval <0){
		    throw new RuntimeException("could not open output file");
		}
		
		IStream outStream = outContainer.addNewStream(0);
		IStreamCoder outStreamCoder = outStream.getStreamCoder();
		
		ICodec codec = ICodec.guessEncodingCodec(null, null, outFileUrl, null, ICodec.Type.CODEC_TYPE_VIDEO); 

		outStreamCoder.setNumPicturesInGroupOfPictures(10);
		outStreamCoder.setCodec(codec);

		outStreamCoder.setBitRate(25000);
		outStreamCoder.setBitRateTolerance(9000);
		outStreamCoder.setPixelType(IPixelFormat.Type.YUV420P);
		outStreamCoder.setHeight(1050);
		outStreamCoder.setWidth(1680);
		outStreamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
		outStreamCoder.setGlobalQuality(0);

		IRational frameRate = IRational.make(3,1);
		outStreamCoder.setFrameRate(frameRate);
		outStreamCoder.setTimeBase(IRational.make(frameRate.getDenominator(), frameRate.getNumerator()));
		frameRate = null;
		
		
	}
}