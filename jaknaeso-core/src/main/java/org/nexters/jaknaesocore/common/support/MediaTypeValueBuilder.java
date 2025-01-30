package org.nexters.jaknaesocore.common.support;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MediaTypeValueBuilder {

  private String mediaType;
  private String charset;

  private MediaTypeValueBuilder(final String mediaType) {
    this.mediaType = mediaType;
  }

  public static MediaTypeValueBuilder builder() {
    return new MediaTypeValueBuilder();
  }

  public static MediaTypeValueBuilder builder(final String mediaType) {
    return new MediaTypeValueBuilder(mediaType);
  }

  public MediaTypeValueBuilder mediaType(final String mediaType) {
    this.mediaType = mediaType;
    return this;
  }

  public MediaTypeValueBuilder charset(final String charset) {
    this.charset = charset;
    return this;
  }

  public String build() {
    if (!hasField(mediaType)) {
      throw new IllegalStateException("mediaType 값이 설정되지 않았습니다.");
    }

    StringBuilder s = new StringBuilder(mediaType);
    if (hasField(charset)) {
      s.append(";charset=").append(charset);
    }
    return s.toString();
  }

  private boolean hasField(final String field) {
    return field != null && !field.isBlank();
  }
}
