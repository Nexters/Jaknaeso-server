package org.nexters.jaknaesocore.common.util;

import static java.util.Map.entry;

import java.util.Map;

public class OrdinalFormatter {

  private static final Map<Integer, String> ORDINALS =
      Map.ofEntries(
          entry(1, "첫번째"),
          entry(2, "두번째"),
          entry(3, "세번째"),
          entry(4, "네번째"),
          entry(5, "다섯번째"),
          entry(6, "여섯번째"),
          entry(7, "일곱번째"),
          entry(8, "여덟번째"),
          entry(9, "아홉번째"),
          entry(10, "열번째"),
          entry(11, "열한번째"),
          entry(12, "열두번째"),
          entry(13, "열세번째"),
          entry(14, "열네번째"),
          entry(15, "열다섯번째"),
          entry(16, "열여섯번째"),
          entry(17, "열일곱번째"),
          entry(18, "열여덟번째"),
          entry(19, "열아홉번째"),
          entry(20, "스무번째"),
          entry(21, "스물한번째"),
          entry(22, "스물두번째"),
          entry(23, "스물세번째"),
          entry(24, "스물네번째"),
          entry(25, "스물다섯번째"),
          entry(26, "스물여섯번째"),
          entry(27, "스물일곱번째"),
          entry(28, "스물여덟번째"),
          entry(29, "스물아홉번째"),
          entry(30, "서른번째"),
          entry(31, "서른한번째"),
          entry(32, "서른두번째"),
          entry(33, "서른세번째"),
          entry(34, "서른네번째"),
          entry(35, "서른다섯번째"),
          entry(36, "서른여섯번째"),
          entry(37, "서른일곱번째"),
          entry(38, "서른여덟번째"),
          entry(39, "서른아홉번째"),
          entry(40, "마흔번째"),
          entry(41, "마흔한번째"),
          entry(42, "마흔두번째"),
          entry(43, "마흔세번째"),
          entry(44, "마흔네번째"),
          entry(45, "마흔다섯번째"),
          entry(46, "마흔여섯번째"),
          entry(47, "마흔일곱번째"),
          entry(48, "마흔여덟번째"),
          entry(49, "마흔아홉번째"),
          entry(50, "쉰번째"),
          entry(51, "쉰한번째"),
          entry(52, "쉰두번째"),
          entry(53, "쉰세번째"),
          entry(54, "쉰네번째"),
          entry(55, "쉰다섯번째"),
          entry(56, "쉰여섯번째"),
          entry(57, "쉰일곱번째"),
          entry(58, "쉰여덟번째"),
          entry(59, "쉰아홉번째"),
          entry(60, "예순번째"),
          entry(61, "예순한번째"),
          entry(62, "예순두번째"),
          entry(63, "예순세번째"),
          entry(64, "예순네번째"),
          entry(65, "예순다섯번째"),
          entry(66, "예순여섯번째"),
          entry(67, "예순일곱번째"),
          entry(68, "예순여덟번째"),
          entry(69, "예순아홉번째"),
          entry(70, "일흔번째"),
          entry(71, "일흔한번째"),
          entry(72, "일흔두번째"),
          entry(73, "일흔세번째"),
          entry(74, "일흔네번째"),
          entry(75, "일흔다섯번째"),
          entry(76, "일흔여섯번째"),
          entry(77, "일흔일곱번째"),
          entry(78, "일흔여덟번째"),
          entry(79, "일흔아홉번째"),
          entry(80, "여든번째"),
          entry(81, "여든한번째"),
          entry(82, "여든두번째"),
          entry(83, "여든세번째"),
          entry(84, "여든네번째"),
          entry(85, "여든다섯번째"),
          entry(86, "여든여섯번째"),
          entry(87, "여든일곱번째"),
          entry(88, "여든여덟번째"),
          entry(89, "여든아홉번째"),
          entry(90, "아흔번째"),
          entry(91, "아흔한번째"),
          entry(92, "아흔두번째"),
          entry(93, "아흔세번째"),
          entry(94, "아흔네번째"),
          entry(95, "아흔다섯번째"),
          entry(96, "아흔여섯번째"),
          entry(97, "아흔일곱번째"),
          entry(98, "아흔여덟번째"),
          entry(99, "아흔아홉번째"),
          entry(100, "백번째"));

  private static String FORMAT = "%s %s";
  private static String DEFAULT_ORDINAL = "첫번째";
  private static String CHARACTER_WORD = "캐릭터";

  public static String getCharacterNo(final Integer ordinalNumber) {
    return String.format(
        FORMAT, ORDINALS.getOrDefault(ordinalNumber, DEFAULT_ORDINAL), CHARACTER_WORD);
  }
}
