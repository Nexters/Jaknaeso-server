package org.nexters.jaknaesoserver.domain.character.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.model.ScaledBigDecimal;
import org.nexters.jaknaesocore.domain.character.model.ValueCharacter;
import org.nexters.jaknaesocore.domain.character.model.ValueReport;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterCommand;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterResponse.CharacterTraitResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterValueReportCommand;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterValueReportResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.CharactersResponse;
import org.nexters.jaknaesocore.domain.character.service.dto.SimpleCharacterResponse;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesoserver.common.support.ControllerTest;
import org.nexters.jaknaesoserver.common.support.WithMockCustomUser;

class CharacterControllerTest extends ControllerTest {

  @WithMockCustomUser
  @Test
  void 캐릭터_목록을_조회한다() throws Exception {

    given(characterService.getCharacters(1L))
        .willReturn(
            new CharactersResponse(
                List.of(
                    SimpleCharacterResponse.builder()
                        .characterNo("첫번째")
                        .characterId(1L)
                        .bundleId(1L)
                        .build())));

    mockMvc
        .perform(
            get("/api/v1/characters")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .queryParam("memberId", "1")
                .with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "character-list-success",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("캐릭터 목록 반환")
                        .tag("Character Domain")
                        .queryParameters(
                            parameterWithName("memberId")
                                .type(SimpleType.NUMBER)
                                .description("멤버 아이디"))
                        .responseFields(
                            fieldWithPath("result")
                                .type(SimpleType.STRING)
                                .description("API 요청 결과 (성공/실패)"),
                            fieldWithPath("data.characters[].characterNo")
                                .type(SimpleType.STRING)
                                .description("캐릭터 회차"),
                            fieldWithPath("data.characters[].characterId")
                                .type(SimpleType.NUMBER)
                                .description("캐릭터 아이디 (캐릭터가 완성되지 않은 경우 null)"),
                            fieldWithPath("data.characters[].bundleId")
                                .type(SimpleType.NUMBER)
                                .description("설문 번들 아이디"),
                            fieldWithPath("data.characters[].isCompleted")
                                .type(SimpleType.BOOLEAN)
                                .description("설문 번들 완료 여부"),
                            fieldWithPath("error").description("에러").optional())
                        .responseSchema(schema("CharactersResponse"))
                        .build())));
  }

  @WithMockCustomUser
  @Test
  void 특정_캐릭터_정보를_조회한다() throws Exception {
    given(characterService.getCharacter(new CharacterCommand(1L, 1L)))
        .willReturn(createCharacterResponse());

    mockMvc
        .perform(
            get("/api/v1/characters/{characterId}", 1)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .queryParam("memberId", "1")
                .with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "character-success",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("캐릭터 목록 반환")
                        .tag("Character Domain")
                        .pathParameters(
                            parameterWithName("characterId")
                                .type(SimpleType.NUMBER)
                                .description("캐릭터 아이디"))
                        .queryParameters(
                            parameterWithName("memberId")
                                .type(SimpleType.NUMBER)
                                .description("멤버 아이디"))
                        .responseFields(
                            fieldWithPath("result")
                                .type(SimpleType.STRING)
                                .description("API 요청 결과 (성공/실패)"),
                            fieldWithPath("data.characterId")
                                .type(SimpleType.NUMBER)
                                .description("캐릭터 아이디"),
                            fieldWithPath("data.characterNo")
                                .type(SimpleType.STRING)
                                .description("캐릭터 회차"),
                            fieldWithPath("data.characterType").description("캐릭터 타입"),
                            fieldWithPath("data.name")
                                .type(SimpleType.STRING)
                                .description("캐릭터 이름"),
                            fieldWithPath("data.description")
                                .type(SimpleType.STRING)
                                .description("캐릭터 설명"),
                            fieldWithPath("data.mainTraits[].description")
                                .type(SimpleType.STRING)
                                .description("캐릭터 주요 특성"),
                            fieldWithPath("data.strengths[].description")
                                .type(SimpleType.STRING)
                                .description("캐릭터 강점"),
                            fieldWithPath("data.weaknesses[].description")
                                .type(SimpleType.STRING)
                                .description("캐릭터 약점"),
                            fieldWithPath("data.startDate")
                                .type(SimpleType.STRING)
                                .description("시작 일자"),
                            fieldWithPath("data.endDate")
                                .type(SimpleType.STRING)
                                .description("종료 일자"),
                            fieldWithPath("error").description("에러").optional())
                        .responseSchema(schema("CharacterResponse"))
                        .build())));
  }

  @WithMockCustomUser
  @Test
  void 현재_캐릭터_정보를_조회한다() throws Exception {

    ValueCharacter valueCharacter = new ValueCharacter("성취를 쫓는 노력가", "성공 캐릭터 설명", Keyword.SUCCESS);
    given(characterService.getCurrentCharacter(1L)).willReturn(createCharacterResponse());

    mockMvc
        .perform(
            get("/api/v1/characters/latest")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .queryParam("memberId", "1")
                .with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "character-success",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("캐릭터 목록 반환")
                        .tag("Character Domain")
                        .queryParameters(
                            parameterWithName("memberId")
                                .type(SimpleType.NUMBER)
                                .description("멤버 아이디"))
                        .responseFields(
                            fieldWithPath("result")
                                .type(SimpleType.STRING)
                                .description("API 요청 결과 (성공/실패)"),
                            fieldWithPath("data.characterId")
                                .type(SimpleType.NUMBER)
                                .description("캐릭터 아이디"),
                            fieldWithPath("data.characterNo")
                                .type(SimpleType.STRING)
                                .description("캐릭터 회차"),
                            fieldWithPath("data.characterType").description("캐릭터 타입"),
                            fieldWithPath("data.name")
                                .type(SimpleType.STRING)
                                .description("캐릭터 이름"),
                            fieldWithPath("data.description")
                                .type(SimpleType.STRING)
                                .description("캐릭터 설명"),
                            fieldWithPath("data.mainTraits[].description")
                                .type(SimpleType.STRING)
                                .description("캐릭터 주요 특성"),
                            fieldWithPath("data.strengths[].description")
                                .type(SimpleType.STRING)
                                .description("캐릭터 강점"),
                            fieldWithPath("data.weaknesses[].description")
                                .type(SimpleType.STRING)
                                .description("캐릭터 약점"),
                            fieldWithPath("data.startDate")
                                .type(SimpleType.STRING)
                                .description("시작 일자"),
                            fieldWithPath("data.endDate")
                                .type(SimpleType.STRING)
                                .description("종료 일자"),
                            fieldWithPath("error").description("에러").optional())
                        .responseSchema(schema("CharacterResponse"))
                        .build())));
  }

  @WithMockCustomUser
  @Test
  void 특정_캐릭터의_가치관_분석_정보를_조회한다() throws Exception {

    given(characterService.getCharacterReport(any(CharacterValueReportCommand.class)))
        .willReturn(
            CharacterValueReportResponse.of(
                List.of(
                    ValueReport.of(
                        Keyword.SUCCESS, ScaledBigDecimal.of(BigDecimal.valueOf(33.33))))));
    mockMvc
        .perform(
            get("/api/v1/characters/{characterId}/report", 1)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .queryParam("memberId", "1")
                .with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "character-value-report-success",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("캐릭터 분석 정보 반환")
                        .tag("Character Domain")
                        .pathParameters(
                            parameterWithName("characterId")
                                .type(SimpleType.NUMBER)
                                .description("캐릭터 아이디"))
                        .queryParameters(
                            parameterWithName("memberId")
                                .type(SimpleType.NUMBER)
                                .description("멤버 아이디"))
                        .responseFields(
                            fieldWithPath("result")
                                .type(SimpleType.STRING)
                                .description("API 요청 결과 (성공/실패)"),
                            fieldWithPath("data.valueReports[].keyword").description("가치관"),
                            fieldWithPath("data.valueReports[].percentage")
                                .description("해당 가치관 비율"),
                            fieldWithPath("error").description("에러").optional())
                        .responseSchema(schema("CharacterValueReportResponse"))
                        .build())));
  }

  private CharacterResponse createCharacterResponse() {
    return CharacterResponse.builder()
        .characterId(1L)
        .characterNo("첫번째")
        .characterType(Keyword.SUCCESS)
        .name("성취를 쫓는 노력가")
        .description(
            "목표를 이루고 영향력을 가지는 것을 중요하게 여기는\n노력가 유형은 끊임없는 노력과 성과를 내는 것을 중요시 여겨요.\n사회에서 의미 있는 위치를 차지하고 싶어하는 야망가!")
        .mainTraits(
            List.of(
                CharacterTraitResponse.builder()
                    .description("목표를 이루기 위해 끊임없이 노력하며, 성장하는 과정에서 보람을 느껴요.")
                    .build()))
        .strengths(
            List.of(
                CharacterTraitResponse.builder()
                    .description("강한 추진력과 목표 지향적인 태도로 원하는 결과를 만들어내요.")
                    .build()))
        .weaknesses(
            List.of(
                CharacterTraitResponse.builder()
                    .description("성과 중심적인 태도가 타인과의 관계에 영향을 줄 수 있어요.")
                    .build()))
        .startDate(LocalDate.now().minusDays(15).toString())
        .endDate(LocalDate.now().toString())
        .build();
  }
}
