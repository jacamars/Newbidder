##Standard SSP Support
Gernerally, any Open RTB SSP will work with RTB4FREE once configured. The following have been integrated with RTB4FREE
in the past:

- admedia  
- adprudence
- adventurefeeds
- appnexus
- atomx
- axonix
- bidswitch
- cappture
- citenko
- epomx 
- fyber
- google
- gotham
- index
- intango
- kadam"
- medianexusnetwork
- nexage
- openssp
- openx
- pubmatic
- republer
- smaato
- smartadserver
- smartyads
- ssphwy
- stroer
- taggify
- vdopia
- vertamedia
- waardx
- wideorbit
- xapads

##Extending to New SSPs
Usually, a standard RTB exchange will work out of the box with RTB4FREE. You will have to configure an endpoint and restart your bidders (see below), but you may not have to write any code for it. However if you need to write specialized code for your DSP
this is a guide on how to do it.

### SSP That Uses RTB Protocol
First make a new class file, we will call it NewExchange.java. Place this new class in the package named
com.jacamars.dsp.rtb.exchanges. This new class should extend BidRequest. You then make the constructors and
the copy() method.

Your secret sauce will be in the call parseSpecial().

It would look something like this:

```
package com.jacamars.dsp.rtb.exchanges;

import java.io.InputStream;
import com.jacamars.dsp.rtb.pojo.BidRequest;

/**
 * A class to handle a new exchange
 * @author Ben M. Faul
 *
 */

public class NewExchange extends BidRequest {

        public NewExchange() {
                super();
                parseSpecial();
        }
        
        public NewExchange(String  in) throws Exception  {
                super(in);
                parseSpecial();
    	}
        
    	/**
    	 * Debugging version of the constructor. Will dump if there is a problem
    	 * @param in InputStream. The JSON input
    	 * @param e String. The exchange name
    	 * @throws Exception will dump the error, and set the blackist flag.
    	 */
    	public NewExchange(InputStream in, String e) throws Exception {
    		super(in,"admedia");
    	}

        /**
         * Make a NewExchange bid request using an input stream.
         * @param in InputStream. The contents of a HTTP post.
         * @throws Exception on JSON errors.
         */
        public NewExchange(InputStream in) throws Exception {
                super(in);
                parseSpecial();
        }
        
        /**
         * Process special NewExchange stuff, sets the exchange name. Setss encoding.
         */
        @Override
        public boolean parseSpecial() {
                setExchange( "newexchange" );
                usesEncodedAdm = false;
                return true;
        }
        
    	/**
    	 * Create a new NewExchange object from this class instance.
    	 * @throws JsonProcessingException on parse errors.
    	 * @throws Exception on stream reading errors
    	 */
    	@Override
    	public NewExchange copy(InputStream in) throws Exception  {
    		NewExchage copy = new NewExchange(in);
    		copy.usesEncodedAdm = usesEncodedAdm;
    		copy.usesGzipResponse = usesGzipResponse;
    		return copy;
    	}
}
```

When you the NewExchange class is called it calls the super class BidRequest which handles the conversion of
text based JSON to JACKSON based JSON. If extra processing is required, say like some non-standard JSON objects, you
would handle them in this method.

###Non RTB Protocol
Handling the non-RTB id more involved, but things start off the same.

```
import java.io.InputStream;
import com.jacamars.dsp.rtb.pojo.BidRequest;

/**
 * A class to handle a new exchange
 * @author Ben M. Faul
 *
 */

public class NewExchange extends BidRequest {

		 private ObjectNode root = BidRequest.factory.objectNode();
        public NewExchange() {

        }
        
        public NewExchange(String  in) throws Exception  {
		    // convert string to input stream 'inputStream'
		    this(inputStream);
    	}
    

        /**
         * Make a NewExchange bid request using an input stream.
         * @param in InputStream. The contents of a HTTP post.
         * @throws Exception on JSON errors.
         */
        public NewExchange(InputStream in) throws Exception {
			// read the input and convert to JSON objects and place on the 'root'.
			// When you are done, set rootNode (from the superclass to 'root' and call setup()'
			
			rootNode = root;
			setup();
        }
        
    
    	/**
    	 * Create a new NewExchange object from this class instance.
    	 * @throws JsonProcessingException on parse errors.
    	 * @throws Exception on stream reading errors
    	 */
    	@Override
    	public NewExchange copy(InputStream in) throws Exception  {
    		NewExchage copy = new NewExchange(in);
    		copy.usesEncodedAdm = usesEncodedAdm;
    		copy.usesGzipResponse = usesGzipResponse;
    		return copy;
    	}
    	
    	
}
```

Here notice you have an ObjectNode called 'root'. Parse whatever your input stream is and map it to a BidRequest object.
When completed, set rootNode from the superclass to 'root', then call the super class setup().

You have to understand the construction of RTB, there's no getting around that. But it's not too hard. The GoogleBidRequest.java class in com.jacamars.exchanges.google is a good example of how to handle non-text JSON data and
convert it to standard RTB in JACKSON format.