package org.nexters.jaknaesocore.domain.sample.exception;

import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.common.support.error.ErrorType;

public class SampleNotFound extends CustomException {

  private final static SampleNotFound INSTANCE = new SampleNotFound();
  public static SampleNotFound getInstance() {
    return INSTANCE;
  }

  public SampleNotFound() {
    super(ErrorType.SAMPLE_NOT_FOUND);
  }
}
