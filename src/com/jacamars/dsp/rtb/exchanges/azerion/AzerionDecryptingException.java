// Copyright OpenX Limited 2010. All Rights Reserved.
package com.jacamars.dsp.rtb.exchanges.azerion;

/**
 * An exception class for OpenX encryption exceptions
 */

public class AzerionDecryptingException extends Exception {
  private static final long serialVersionUID = 1L;

  public AzerionDecryptingException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
