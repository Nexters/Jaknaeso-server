package org.nexters.jaknaesocore.domain.auth.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AppleAuthorization {

  @Getter private final String sub;

  @Getter private final String email;
}
