package org.nexters.jaknaesocore.domain.survey.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.assertj.core.api.BDDAssertions.tuple;
import static org.nexters.jaknaesocore.domain.survey.model.Keyword.SELF_DIRECTION;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.IntegrationTest;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.domain.character.model.CharacterRecord;
import org.nexters.jaknaesocore.domain.character.model.ValueCharacter;
import org.nexters.jaknaesocore.domain.character.repository.CharacterRecordRepository;
import org.nexters.jaknaesocore.domain.character.repository.ValueCharacterRepository;
import org.nexters.jaknaesocore.domain.character.repository.ValueReportRepository;
import org.nexters.jaknaesocore.domain.character.service.CharacterService;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.survey.dto.OnboardingSubmissionResult;
import org.nexters.jaknaesocore.domain.survey.dto.OnboardingSubmissionsCommand;
import org.nexters.jaknaesocore.domain.survey.dto.OnboardingSurveyResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyHistoryResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveySubmissionCommand;
import org.nexters.jaknaesocore.domain.survey.dto.SurveySubmissionHistoryCommand;
import org.nexters.jaknaesocore.domain.survey.dto.SurveySubmissionHistoryResponse;
import org.nexters.jaknaesocore.domain.survey.model.BalanceSurvey;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScore;
import org.nexters.jaknaesocore.domain.survey.model.MultipleChoiceSurvey;
import org.nexters.jaknaesocore.domain.survey.model.OnboardingSurvey;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.nexters.jaknaesocore.domain.survey.model.SurveyOption;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyBundleRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyOptionRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveyRepository;
import org.nexters.jaknaesocore.domain.survey.repository.SurveySubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

class SurveyServiceTest extends IntegrationTest {

  @Autowired private SurveyService surveyService;

  @Autowired private MemberRepository memberRepository;
  @Autowired private SurveySubmissionRepository surveySubmissionRepository;
  @Autowired private SurveyBundleRepository surveyBundleRepository;
  @Autowired private SurveyRepository surveyRepository;
  @Autowired private SurveyOptionRepository surveyOptionRepository;
  @Autowired private CharacterRecordRepository characterRecordRepository;
  @Autowired private ValueReportRepository valueReportRepository;
  @Autowired private ValueCharacterRepository valueCharacterRepository;

  @MockitoSpyBean private CharacterService characterService;

  @AfterEach
  void tearDown() {
    valueReportRepository.deleteAllInBatch();
    characterRecordRepository.deleteAllInBatch();
    surveySubmissionRepository.deleteAllInBatch();
    surveyOptionRepository.deleteAllInBatch();
    surveyRepository.deleteAllInBatch();
    surveyBundleRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
    valueCharacterRepository.deleteAllInBatch();
  }

  @DisplayName("설문을 조회한다.")
  @Test
  void getNextSurvey() {
    // given
    Member member = Member.create("name", "email");
    memberRepository.save(member);
    SurveyBundle surveyBundle = new SurveyBundle();
    surveyBundleRepository.save(surveyBundle);
    BalanceSurvey balanceSurvey = new BalanceSurvey("질문내용", surveyBundle);
    MultipleChoiceSurvey multipleSurvey = new MultipleChoiceSurvey("다음 질문내용", surveyBundle);
    surveyRepository.saveAll(List.of(balanceSurvey, multipleSurvey));
    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ADVENTURE).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());
    SurveyOption option =
        SurveyOption.builder().survey(balanceSurvey).scores(scores).content("질문 옵션 내용").build();
    SurveyOption multipleOption1 =
        SurveyOption.builder().survey(multipleSurvey).scores(scores).content("1점").build();
    SurveyOption multipleOption2 =
        SurveyOption.builder().survey(multipleSurvey).scores(scores).content("2점").build();
    surveyOptionRepository.saveAll(List.of(option, multipleOption1, multipleOption2));

    // when
    SurveyResponse survey = surveyService.getNextSurvey(surveyBundle.getId(), member.getId());
    // then
    then(survey)
        .extracting("id", "contents", "surveyType")
        .containsExactly(balanceSurvey.getId(), "질문내용", "BALANCE");
    then(survey.options())
        .extracting("id", "optionContents")
        .containsExactly(tuple(option.getId(), "질문 옵션 내용"));
  }

  @Disabled("클라이언트의 온보딩 작업이 완료되면 테스트를 진행합니다.")
  @Test
  void 설문_기록이_없으면_접근_할_수_없다() {
    // given
    Member member = Member.create("나민혁", "test@test.com");
    memberRepository.save(member);
    SurveyBundle surveyBundle = new SurveyBundle();
    surveyBundleRepository.save(surveyBundle);
    BalanceSurvey survey1 = new BalanceSurvey("대학 동기 모임에서 나의 승진 이야기가 나왔습니다", surveyBundle);
    BalanceSurvey survey2 = new BalanceSurvey("회사에서 팀 리더로 뽑혔습니다", surveyBundle);
    MultipleChoiceSurvey survey3 = new MultipleChoiceSurvey("나의 행복 지수는", surveyBundle);
    MultipleChoiceSurvey survey4 = new MultipleChoiceSurvey("나는 노는게 좋다.", surveyBundle);

    surveyRepository.saveAll(List.of(survey1, survey2, survey3, survey4));
    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ADVENTURE).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());
    SurveyOption option1 =
        SurveyOption.builder().survey(survey1).scores(scores).content("한다.").build();
    SurveyOption option2 =
        SurveyOption.builder().survey(survey1).scores(scores).content("안한다.").build();
    SurveyOption option3 =
        SurveyOption.builder().survey(survey2).scores(scores).content("한다.").build();
    SurveyOption option4 =
        SurveyOption.builder().survey(survey2).scores(scores).content("안한다.").build();
    SurveyOption option5 =
        SurveyOption.builder().survey(survey3).scores(scores).content("3점").build();
    SurveyOption option6 =
        SurveyOption.builder().survey(survey4).scores(scores).content("4점").build();
    surveyOptionRepository.saveAll(List.of(option1, option2, option3, option4, option5, option6));
    // when
    // then
    thenThrownBy(() -> surveyService.getSurveyHistory(member.getId()))
        .isInstanceOf(CustomException.class)
        .isEqualTo(CustomException.NOT_PROCEED_ONBOARDING);
  }

  @Test
  void 설문_기록이_일정_개수_이하이면_기록과_함께_번들ID를_제공한다() {
    // given
    Member member = Member.create("나민혁", "test@test.com");
    memberRepository.save(member);
    SurveyBundle surveyBundle = new SurveyBundle();
    surveyBundleRepository.save(surveyBundle);
    BalanceSurvey survey1 = new BalanceSurvey("대학 동기 모임에서 나의 승진 이야기가 나왔습니다", surveyBundle);
    BalanceSurvey survey2 = new BalanceSurvey("회사에서 팀 리더로 뽑혔습니다", surveyBundle);
    MultipleChoiceSurvey survey3 = new MultipleChoiceSurvey("나의 행복 지수는", surveyBundle);
    MultipleChoiceSurvey survey4 = new MultipleChoiceSurvey("나는 노는게 좋다.", surveyBundle);

    surveyRepository.saveAll(List.of(survey1, survey2, survey3, survey4));
    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ADVENTURE).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());
    SurveyOption option1 =
        SurveyOption.builder().survey(survey1).scores(scores).content("한다.").build();
    SurveyOption option2 =
        SurveyOption.builder().survey(survey1).scores(scores).content("안한다.").build();
    SurveyOption option3 =
        SurveyOption.builder().survey(survey2).scores(scores).content("한다.").build();
    SurveyOption option4 =
        SurveyOption.builder().survey(survey2).scores(scores).content("안한다.").build();
    SurveyOption option5 =
        SurveyOption.builder().survey(survey3).scores(scores).content("3점").build();
    SurveyOption option6 =
        SurveyOption.builder().survey(survey4).scores(scores).content("4점").build();
    surveyOptionRepository.saveAll(List.of(option1, option2, option3, option4, option5, option6));

    SurveySubmission submission1 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option1)
            .survey(survey1)
            .submittedAt(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
            .build();
    SurveySubmission submission2 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option4)
            .survey(survey2)
            .submittedAt(LocalDateTime.of(2025, 1, 2, 12, 0, 0))
            .build();
    surveySubmissionRepository.save(submission1);
    surveySubmissionRepository.save(submission2);
    // when
    SurveyHistoryResponse response = surveyService.getSurveyHistory(member.getId());
    // then
    then(response)
        .extracting("bundleId", "nextSurveyIndex", "isCompleted")
        .containsExactly(surveyBundle.getId(), 3, false);

    then(response.surveyHistoryDetails())
        .extracting("submissionId", "index")
        .containsExactly(tuple(submission1.getId(), 1), tuple(submission2.getId(), 2));
  }

  @Test
  void 오늘_설문을_이미_제출_했다면_제출완료_상태이다() {
    // given
    LocalDateTime now = LocalDateTime.now();
    Member member = Member.create("나민혁", "test@test.com");
    memberRepository.save(member);
    SurveyBundle surveyBundle = new SurveyBundle();
    surveyBundleRepository.save(surveyBundle);
    BalanceSurvey survey1 = new BalanceSurvey("대학 동기 모임에서 나의 승진 이야기가 나왔습니다", surveyBundle);
    BalanceSurvey survey2 = new BalanceSurvey("회사에서 팀 리더로 뽑혔습니다", surveyBundle);
    MultipleChoiceSurvey survey3 = new MultipleChoiceSurvey("나의 행복 지수는", surveyBundle);
    MultipleChoiceSurvey survey4 = new MultipleChoiceSurvey("나는 노는게 좋다.", surveyBundle);

    surveyRepository.saveAll(List.of(survey1, survey2, survey3, survey4));
    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ADVENTURE).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());
    SurveyOption option1 =
        SurveyOption.builder().survey(survey1).scores(scores).content("한다.").build();
    SurveyOption option2 =
        SurveyOption.builder().survey(survey1).scores(scores).content("안한다.").build();
    SurveyOption option3 =
        SurveyOption.builder().survey(survey2).scores(scores).content("한다.").build();
    SurveyOption option4 =
        SurveyOption.builder().survey(survey2).scores(scores).content("안한다.").build();
    SurveyOption option5 =
        SurveyOption.builder().survey(survey3).scores(scores).content("3점").build();
    SurveyOption option6 =
        SurveyOption.builder().survey(survey4).scores(scores).content("4점").build();
    surveyOptionRepository.saveAll(List.of(option1, option2, option3, option4, option5, option6));

    SurveySubmission submission1 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option1)
            .survey(survey1)
            .submittedAt(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
            .build();
    SurveySubmission submission2 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option2)
            .survey(survey2)
            .submittedAt(LocalDateTime.of(2025, 1, 2, 12, 0, 0))
            .build();
    SurveySubmission submission3 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option3)
            .survey(survey3)
            .submittedAt(LocalDateTime.of(2025, 1, 3, 12, 0, 0))
            .build();
    SurveySubmission submission4 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option4)
            .survey(survey4)
            .submittedAt(now)
            .build();

    surveySubmissionRepository.saveAll(List.of(submission1, submission2, submission3, submission4));
    // when
    SurveyHistoryResponse response = surveyService.getSurveyHistory(member.getId());
    // then
    then(response)
        .extracting("bundleId", "nextSurveyIndex", "isCompleted")
        .containsExactly(surveyBundle.getId(), 4, true);

    then(response.surveyHistoryDetails())
        .extracting("submissionId", "index")
        .containsExactly(
            tuple(submission1.getId(), 1),
            tuple(submission2.getId(), 2),
            tuple(submission3.getId(), 3),
            tuple(submission4.getId(), 4));
  }

  @Test
  void 설문_기록이_번들_크기만큼_있으면_다음_번들ID를_제공한다() {
    // given
    Member member = Member.create("나민혁", "test@test.com");
    memberRepository.save(member);
    SurveyBundle surveyBundle1 = new SurveyBundle();
    SurveyBundle surveyBundle2 = new SurveyBundle();

    surveyBundleRepository.saveAll(List.of(surveyBundle1, surveyBundle2));

    BalanceSurvey survey1 = new BalanceSurvey("대학 동기 모임에서 나의 승진 이야기가 나왔습니다", surveyBundle1);
    BalanceSurvey survey2 = new BalanceSurvey("회사에서 팀 리더로 뽑혔습니다", surveyBundle1);
    MultipleChoiceSurvey survey3 = new MultipleChoiceSurvey("나의 행복 지수는", surveyBundle1);
    MultipleChoiceSurvey survey4 = new MultipleChoiceSurvey("나는 노는게 좋다.", surveyBundle1);

    surveyRepository.saveAll(List.of(survey1, survey2, survey3, survey4));

    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ADVENTURE).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());

    SurveyOption option1 =
        SurveyOption.builder().survey(survey1).scores(scores).content("한다.").build();
    SurveyOption option2 =
        SurveyOption.builder().survey(survey2).scores(scores).content("한다.").build();
    SurveyOption option3 =
        SurveyOption.builder().survey(survey3).scores(scores).content("3점").build();
    SurveyOption option4 =
        SurveyOption.builder().survey(survey4).scores(scores).content("4점").build();

    surveyOptionRepository.saveAll(List.of(option1, option2, option3, option4));

    SurveySubmission submission1 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option1)
            .survey(survey1)
            .submittedAt(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
            .build();
    SurveySubmission submission2 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option2)
            .survey(survey2)
            .submittedAt(LocalDateTime.of(2025, 1, 2, 12, 0, 0))
            .build();
    SurveySubmission submission3 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option3)
            .survey(survey3)
            .submittedAt(LocalDateTime.of(2025, 1, 3, 12, 0, 0))
            .build();
    SurveySubmission submission4 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option4)
            .survey(survey4)
            .submittedAt(LocalDateTime.of(2025, 1, 4, 12, 0, 0))
            .build();

    surveySubmissionRepository.saveAll(List.of(submission1, submission2, submission3, submission4));

    // when
    SurveyHistoryResponse response = surveyService.getSurveyHistory(member.getId());

    // then
    then(response)
        .extracting("bundleId", "nextSurveyIndex", "surveyHistoryDetails", "isCompleted")
        .containsExactly(surveyBundle1.getId() + 1L, 1, List.of(), false);
  }

  @Test
  void 진행할_다음_설문_번들이_존재하지_않으면_예외를_발생한다() {
    // given
    Member member = Member.create("나민혁", "test@test.com");
    memberRepository.save(member);
    SurveyBundle surveyBundle = new SurveyBundle();

    surveyBundleRepository.save(surveyBundle);

    BalanceSurvey survey1 = new BalanceSurvey("대학 동기 모임에서 나의 승진 이야기가 나왔습니다", surveyBundle);
    BalanceSurvey survey2 = new BalanceSurvey("회사에서 팀 리더로 뽑혔습니다", surveyBundle);
    MultipleChoiceSurvey survey3 = new MultipleChoiceSurvey("나의 행복 지수는", surveyBundle);
    MultipleChoiceSurvey survey4 = new MultipleChoiceSurvey("나는 노는게 좋다.", surveyBundle);

    surveyRepository.saveAll(List.of(survey1, survey2, survey3, survey4));

    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ADVENTURE).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());

    SurveyOption option1 =
        SurveyOption.builder().survey(survey1).scores(scores).content("한다.").build();
    SurveyOption option2 =
        SurveyOption.builder().survey(survey2).scores(scores).content("한다.").build();
    SurveyOption option3 =
        SurveyOption.builder().survey(survey3).scores(scores).content("3점").build();
    SurveyOption option4 =
        SurveyOption.builder().survey(survey4).scores(scores).content("4점").build();

    surveyOptionRepository.saveAll(List.of(option1, option2, option3, option4));

    SurveySubmission submission1 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option1)
            .survey(survey1)
            .submittedAt(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
            .build();
    SurveySubmission submission2 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option2)
            .survey(survey2)
            .submittedAt(LocalDateTime.of(2025, 1, 2, 12, 0, 0))
            .build();
    SurveySubmission submission3 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option3)
            .survey(survey3)
            .submittedAt(LocalDateTime.of(2025, 1, 3, 12, 0, 0))
            .build();
    SurveySubmission submission4 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option4)
            .survey(survey4)
            .submittedAt(LocalDateTime.of(2025, 1, 4, 12, 0, 0))
            .build();

    surveySubmissionRepository.saveAll(List.of(submission1, submission2, submission3, submission4));
    // when
    // then
    thenThrownBy(() -> surveyService.getSurveyHistory(member.getId()))
        .isInstanceOf(CustomException.class)
        .isEqualTo(CustomException.NO_MORE_SURVEY_BUNDLES);
  }

  @Test
  void 개인의_번들_내부의_제출_결과를_가져온다() {
    // given
    Member member = Member.create("나민혁", "test@test.com");
    memberRepository.save(member);
    SurveyBundle surveyBundle = new SurveyBundle();

    surveyBundleRepository.save(surveyBundle);

    BalanceSurvey survey1 = new BalanceSurvey("돈 vs 명예", surveyBundle);
    BalanceSurvey survey2 = new BalanceSurvey("사랑 vs 우정", surveyBundle);
    MultipleChoiceSurvey survey3 = new MultipleChoiceSurvey("나의 행복 지수는", surveyBundle);
    MultipleChoiceSurvey survey4 = new MultipleChoiceSurvey("나는 노는게 좋다.", surveyBundle);

    surveyRepository.saveAll(List.of(survey1, survey2, survey3, survey4));

    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ADVENTURE).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());

    SurveyOption option1 =
        SurveyOption.builder().survey(survey1).scores(scores).content("돈").build();
    SurveyOption option2 =
        SurveyOption.builder().survey(survey2).scores(scores).content("사랑").build();
    SurveyOption option3 =
        SurveyOption.builder().survey(survey3).scores(scores).content("3점").build();
    SurveyOption option4 =
        SurveyOption.builder().survey(survey4).scores(scores).content("4점").build();

    surveyOptionRepository.saveAll(List.of(option1, option2, option3, option4));
    SurveySubmission submission1 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option1)
            .survey(survey1)
            .submittedAt(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
            .build();
    SurveySubmission submission2 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option2)
            .survey(survey2)
            .submittedAt(LocalDateTime.of(2025, 1, 2, 0, 0, 0))
            .build();
    SurveySubmission submission3 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option3)
            .survey(survey3)
            .submittedAt(LocalDateTime.of(2025, 1, 3, 0, 0, 0))
            .build();
    SurveySubmission submission4 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option4)
            .survey(survey4)
            .retrospective("당연히 노는게 좋은거 아닌가?")
            .submittedAt(LocalDateTime.of(2025, 2, 1, 0, 0, 0))
            .build();

    surveySubmissionRepository.saveAll(List.of(submission1, submission2, submission3, submission4));

    // when
    SurveySubmissionHistoryResponse response =
        surveyService.getSurveySubmissionHistory(
            new SurveySubmissionHistoryCommand(member.getId(), surveyBundle.getId()));
    // then
    then(response.surveyRecords())
        .hasSize(4)
        .extracting("question", "answer", "retrospective", "submittedAt")
        .containsExactly(
            tuple("돈 vs 명예", "돈", null, "2025.01.01"),
            tuple("사랑 vs 우정", "사랑", null, "2025.01.02"),
            tuple("나의 행복 지수는", "3점", null, "2025.01.03"),
            tuple("나는 노는게 좋다.", "4점", "당연히 노는게 좋은거 아닌가?", "2025.02.01"));
  }

  @Test
  void 온보딩_설문_목록을_가져온다() {
    // given
    Member member = Member.create("나민혁", "test@test.com");
    memberRepository.save(member);
    SurveyBundle surveyBundle = new SurveyBundle();

    surveyBundleRepository.save(surveyBundle);

    OnboardingSurvey survey1 =
        new OnboardingSurvey(
            "새로운 아이디어를 갖고 창의적인 것이 그/그녀에게 중요하다. 그/그녀는 일을 자신만의 독특한 방식으로 하는 것을 좋아한다.", surveyBundle);
    OnboardingSurvey survey2 =
        new OnboardingSurvey("그/그녀에게 부자가 되는 것은 중요하다. 많은 돈과 비싼 물건들을 가지길 원한다.", surveyBundle);
    OnboardingSurvey survey3 =
        new OnboardingSurvey(
            "세상의 모든 사람들이 평등하게 대우받아야 한다고 생각한다. 그/그녀는 모든 사람이 인생에서 동등한 기회를 가져야 한다고 믿는다.",
            surveyBundle);
    OnboardingSurvey survey4 =
        new OnboardingSurvey(
            "그/그녀에게 자신의 능력을 보여주는 것이 매우 중요하다. 사람들이 자신이 하는 일을 인정해주길 바란다.", surveyBundle);

    surveyRepository.saveAll(List.of(survey1, survey2, survey3, survey4));

    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ADVENTURE).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());

    SurveyOption option1 =
        SurveyOption.builder().survey(survey1).scores(scores).content("전혀 나와 같지않다.").build();
    SurveyOption option2 =
        SurveyOption.builder().survey(survey2).scores(scores).content("나와 같지 않다.").build();
    SurveyOption option3 =
        SurveyOption.builder().survey(survey3).scores(scores).content("나와 조금 같다.").build();
    SurveyOption option4 =
        SurveyOption.builder().survey(survey4).scores(scores).content("나와 같다.").build();

    surveyOptionRepository.saveAll(List.of(option1, option2, option3, option4));
    // when
    OnboardingSurveyResponse response = surveyService.getOnboardingSurveys();
    // then
    then(response.surveyResponses())
        .extracting("id", "contents", "surveyType")
        .containsExactly(
            tuple(
                survey1.getId(),
                "새로운 아이디어를 갖고 창의적인 것이 그/그녀에게 중요하다. 그/그녀는 일을 자신만의 독특한 방식으로 하는 것을 좋아한다.",
                "ONBOARDING"),
            tuple(survey2.getId(), "그/그녀에게 부자가 되는 것은 중요하다. 많은 돈과 비싼 물건들을 가지길 원한다.", "ONBOARDING"),
            tuple(
                survey3.getId(),
                "세상의 모든 사람들이 평등하게 대우받아야 한다고 생각한다. 그/그녀는 모든 사람이 인생에서 동등한 기회를 가져야 한다고 믿는다.",
                "ONBOARDING"),
            tuple(
                survey4.getId(),
                "그/그녀에게 자신의 능력을 보여주는 것이 매우 중요하다. 사람들이 자신이 하는 일을 인정해주길 바란다.",
                "ONBOARDING"));
  }

  @Transactional
  @Test
  void 온보딩_설문을_제출한다() {
    // given
    Member member = Member.create("나민혁", "test@test.com");
    memberRepository.save(member);
    SurveyBundle surveyBundle = new SurveyBundle();

    final ValueCharacter valueCharacter =
        valueCharacterRepository.save(
            ValueCharacter.builder()
                .name("아낌없이 주는 보금자리 유형")
                .description("보금자리 유형 설명")
                .keyword(Keyword.BENEVOLENCE)
                .build());

    surveyBundleRepository.save(surveyBundle);

    OnboardingSurvey survey1 =
        new OnboardingSurvey(
            "새로운 아이디어를 갖고 창의적인 것이 그/그녀에게 중요하다. 그/그녀는 일을 자신만의 독특한 방식으로 하는 것을 좋아한다.", surveyBundle);
    OnboardingSurvey survey2 =
        new OnboardingSurvey("그/그녀에게 부자가 되는 것은 중요하다. 많은 돈과 비싼 물건들을 가지길 원한다.", surveyBundle);
    OnboardingSurvey survey3 =
        new OnboardingSurvey(
            "세상의 모든 사람들이 평등하게 대우받아야 한다고 생각한다. 그/그녀는 모든 사람이 인생에서 동등한 기회를 가져야 한다고 믿는다.",
            surveyBundle);
    OnboardingSurvey survey4 =
        new OnboardingSurvey(
            "그/그녀에게 자신의 능력을 보여주는 것이 매우 중요하다. 사람들이 자신이 하는 일을 인정해주길 바란다.", surveyBundle);

    surveyRepository.saveAll(List.of(survey1, survey2, survey3, survey4));

    List<KeywordScore> scores1 =
        List.of(
            KeywordScore.builder().keyword(Keyword.ADVENTURE).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());
    List<KeywordScore> scores2 =
        List.of(
            KeywordScore.builder()
                .keyword(Keyword.ADVENTURE)
                .score(BigDecimal.valueOf(-1))
                .build());

    SurveyOption option1 =
        SurveyOption.builder().survey(survey1).scores(scores1).content("전혀 나와 같지않다.").build();
    SurveyOption option2 =
        SurveyOption.builder().survey(survey2).scores(scores1).content("나와 같지 않다.").build();
    SurveyOption option3 =
        SurveyOption.builder().survey(survey3).scores(scores2).content("나와 조금 같다.").build();
    SurveyOption option4 =
        SurveyOption.builder().survey(survey4).scores(scores2).content("나와 같다.").build();

    surveyOptionRepository.saveAll(List.of(option1, option2, option3, option4));
    LocalDateTime submittedAt = LocalDateTime.of(2025, 2, 13, 18, 25, 0);

    OnboardingSubmissionsCommand command =
        new OnboardingSubmissionsCommand(
            List.of(
                new OnboardingSubmissionResult(survey1.getId(), option1.getId()),
                new OnboardingSubmissionResult(survey2.getId(), option2.getId()),
                new OnboardingSubmissionResult(survey3.getId(), option3.getId()),
                new OnboardingSubmissionResult(survey4.getId(), option4.getId())),
            member.getId());
    // when
    surveyService.submitOnboardingSurvey(command);
    // then
    List<SurveySubmission> submissions = surveySubmissionRepository.findAll();

    then(submissions).hasSize(4);
    then(submissions)
        .extracting("member.id", "survey.id", "selectedOption.id")
        .containsExactlyInAnyOrder(
            tuple(member.getId(), survey1.getId(), option1.getId()),
            tuple(member.getId(), survey2.getId(), option2.getId()),
            tuple(member.getId(), survey3.getId(), option3.getId()),
            tuple(member.getId(), survey4.getId(), option4.getId()));
    then(member).isNotNull();
    then(characterRecordRepository.findAll())
        .hasSize(1)
        .extracting(
            "ordinalNumber", "characterNo", "member.id", "surveyBundle.id", "valueCharacter.id")
        .containsExactly(
            tuple(1, "첫번째 캐릭터", member.getId(), surveyBundle.getId(), valueCharacter.getId()));
  }

  @Transactional
  @Test
  void 온보딩_설문을_제출할_때_동일한_설문에_대한_요청이_있으면_후순위_요청이_저장된다() {
    // given
    Member member = Member.create("나민혁", "test@test.com");
    memberRepository.save(member);
    SurveyBundle surveyBundle = new SurveyBundle();

    final ValueCharacter valueCharacter =
        valueCharacterRepository.save(
            ValueCharacter.builder()
                .name("아낌없이 주는 보금자리 유형")
                .description("보금자리 유형 설명")
                .keyword(Keyword.BENEVOLENCE)
                .build());

    surveyBundleRepository.save(surveyBundle);

    OnboardingSurvey survey1 =
        new OnboardingSurvey(
            "새로운 아이디어를 갖고 창의적인 것이 그/그녀에게 중요하다. 그/그녀는 일을 자신만의 독특한 방식으로 하는 것을 좋아한다.", surveyBundle);
    OnboardingSurvey survey2 =
        new OnboardingSurvey("그/그녀에게 부자가 되는 것은 중요하다. 많은 돈과 비싼 물건들을 가지길 원한다.", surveyBundle);
    OnboardingSurvey survey3 =
        new OnboardingSurvey(
            "세상의 모든 사람들이 평등하게 대우받아야 한다고 생각한다. 그/그녀는 모든 사람이 인생에서 동등한 기회를 가져야 한다고 믿는다.",
            surveyBundle);
    OnboardingSurvey survey4 =
        new OnboardingSurvey(
            "그/그녀에게 자신의 능력을 보여주는 것이 매우 중요하다. 사람들이 자신이 하는 일을 인정해주길 바란다.", surveyBundle);

    surveyRepository.saveAll(List.of(survey1, survey2, survey3, survey4));

    List<KeywordScore> scores1 =
        List.of(
            KeywordScore.builder().keyword(Keyword.ADVENTURE).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());
    List<KeywordScore> scores2 =
        List.of(
            KeywordScore.builder()
                .keyword(Keyword.ADVENTURE)
                .score(BigDecimal.valueOf(-1))
                .build());

    SurveyOption option0 =
        SurveyOption.builder().survey(survey1).scores(scores1).content("전혀 나와 같지않다.").build();
    SurveyOption option1 =
        SurveyOption.builder().survey(survey1).scores(scores1).content("전혀 나와 같지않다.").build();
    SurveyOption option2 =
        SurveyOption.builder().survey(survey2).scores(scores1).content("나와 같지 않다.").build();
    SurveyOption option3 =
        SurveyOption.builder().survey(survey3).scores(scores2).content("나와 조금 같다.").build();
    SurveyOption option4 =
        SurveyOption.builder().survey(survey4).scores(scores2).content("나와 같다.").build();

    surveyOptionRepository.saveAll(List.of(option0, option1, option2, option3, option4));
    LocalDateTime submittedAt = LocalDateTime.of(2025, 2, 13, 18, 25, 0);

    OnboardingSubmissionsCommand command =
        new OnboardingSubmissionsCommand(
            List.of(
                new OnboardingSubmissionResult(survey1.getId(), option0.getId()),
                new OnboardingSubmissionResult(survey1.getId(), option1.getId()),
                new OnboardingSubmissionResult(survey2.getId(), option2.getId()),
                new OnboardingSubmissionResult(survey3.getId(), option3.getId()),
                new OnboardingSubmissionResult(survey4.getId(), option4.getId())),
            member.getId());
    // when
    surveyService.submitOnboardingSurvey(command);
    // then
    List<SurveySubmission> submissions = surveySubmissionRepository.findAll();

    then(submissions).hasSize(4);
    then(submissions)
        .extracting("member.id", "survey.id", "selectedOption.id")
        .containsExactlyInAnyOrder(
            tuple(member.getId(), survey1.getId(), option1.getId()),
            tuple(member.getId(), survey2.getId(), option2.getId()),
            tuple(member.getId(), survey3.getId(), option3.getId()),
            tuple(member.getId(), survey4.getId(), option4.getId()));
    then(member).isNotNull();
    then(characterRecordRepository.findAll())
        .hasSize(1)
        .extracting(
            "ordinalNumber", "characterNo", "member.id", "surveyBundle.id", "valueCharacter.id")
        .containsExactly(
            tuple(1, "첫번째 캐릭터", member.getId(), surveyBundle.getId(), valueCharacter.getId()));
  }

  @Test
  void 온보딩을_이미_완료했으면_진행할_수_없다() {
    // given
    Member member = Member.create("나민혁", "test@test.com");
    ReflectionTestUtils.setField(
        member, "completedOnboardingAt", LocalDateTime.of(2025, 1, 1, 1, 1));
    memberRepository.save(member);
    SurveyBundle surveyBundle = new SurveyBundle();

    final ValueCharacter valueCharacter =
        valueCharacterRepository.save(
            ValueCharacter.builder()
                .name("아낌없이 주는 보금자리 유형")
                .description("보금자리 유형 설명")
                .keyword(Keyword.BENEVOLENCE)
                .build());

    surveyBundleRepository.save(surveyBundle);

    OnboardingSurvey survey1 =
        new OnboardingSurvey(
            "새로운 아이디어를 갖고 창의적인 것이 그/그녀에게 중요하다. 그/그녀는 일을 자신만의 독특한 방식으로 하는 것을 좋아한다.", surveyBundle);
    OnboardingSurvey survey2 =
        new OnboardingSurvey("그/그녀에게 부자가 되는 것은 중요하다. 많은 돈과 비싼 물건들을 가지길 원한다.", surveyBundle);
    OnboardingSurvey survey3 =
        new OnboardingSurvey(
            "세상의 모든 사람들이 평등하게 대우받아야 한다고 생각한다. 그/그녀는 모든 사람이 인생에서 동등한 기회를 가져야 한다고 믿는다.",
            surveyBundle);
    OnboardingSurvey survey4 =
        new OnboardingSurvey(
            "그/그녀에게 자신의 능력을 보여주는 것이 매우 중요하다. 사람들이 자신이 하는 일을 인정해주길 바란다.", surveyBundle);

    surveyRepository.saveAll(List.of(survey1, survey2, survey3, survey4));

    List<KeywordScore> scores1 =
        List.of(
            KeywordScore.builder().keyword(Keyword.ADVENTURE).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());
    List<KeywordScore> scores2 =
        List.of(
            KeywordScore.builder()
                .keyword(Keyword.ADVENTURE)
                .score(BigDecimal.valueOf(-1))
                .build());

    SurveyOption option1 =
        SurveyOption.builder().survey(survey1).scores(scores1).content("전혀 나와 같지않다.").build();
    SurveyOption option2 =
        SurveyOption.builder().survey(survey2).scores(scores1).content("나와 같지 않다.").build();
    SurveyOption option3 =
        SurveyOption.builder().survey(survey3).scores(scores2).content("나와 조금 같다.").build();
    SurveyOption option4 =
        SurveyOption.builder().survey(survey4).scores(scores2).content("나와 같다.").build();

    surveyOptionRepository.saveAll(List.of(option1, option2, option3, option4));
    LocalDateTime submittedAt = LocalDateTime.of(2025, 2, 13, 18, 25, 0);

    OnboardingSubmissionsCommand command =
        new OnboardingSubmissionsCommand(
            List.of(
                new OnboardingSubmissionResult(survey1.getId(), option1.getId()),
                new OnboardingSubmissionResult(survey2.getId(), option2.getId()),
                new OnboardingSubmissionResult(survey3.getId(), option3.getId()),
                new OnboardingSubmissionResult(survey4.getId(), option4.getId())),
            member.getId());
    // when
    thenThrownBy(() -> surveyService.submitOnboardingSurvey(command))
        .isEqualTo(CustomException.ALREADY_COMPLETED_SURVEY_BUNDLE);
  }

  @DisplayName("submitSurvey 메서드는")
  @Nested
  class submitSurvey {

    @DisplayName("회원이 존재하지 않으면")
    @Nested
    class whenMemberNotFound {

      @Test
      @DisplayName("예외를 발생시킨다")
      void throwMemberNotFoundException() {
        // given
        Member member = Member.create("나민혁", "test@test.com");
        memberRepository.save(member);
        SurveyBundle surveyBundle = new SurveyBundle();
        surveyBundleRepository.save(surveyBundle);
        BalanceSurvey balanceSurvey = new BalanceSurvey("질문내용", surveyBundle);
        surveyRepository.save(balanceSurvey);
        List<KeywordScore> scores =
            List.of(
                KeywordScore.builder().keyword(Keyword.ADVENTURE).score(BigDecimal.ONE).build(),
                KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());
        SurveyOption option =
            SurveyOption.builder().survey(balanceSurvey).scores(scores).content("질문 옵션 내용").build();
        surveyOptionRepository.save(option);

        SurveySubmissionCommand command =
            new SurveySubmissionCommand(option.getId(), balanceSurvey.getId(), 0L, "나는 행복한게 좋으니까");

        // when
        // then
        thenThrownBy(() -> surveyService.submitSurvey(command))
            .isEqualTo(CustomException.MEMBER_NOT_FOUND);
      }
    }

    @DisplayName("첫 설문을 시도하면, ")
    @Nested
    class whenFirstSubmitAndNotCompleted {

      @Test
      void 설문에_응답을_제출하고_빈_캐릭터를_생성한다() {
        // given
        Member member = Member.create("나민혁", "test@test.com");
        memberRepository.save(member);

        final ValueCharacter valueCharacter =
            valueCharacterRepository.save(
                ValueCharacter.builder()
                    .name("아낌없이 주는 보금자리 유형")
                    .description("보금자리 유형 설명")
                    .keyword(Keyword.BENEVOLENCE)
                    .build());

        final SurveyBundle onboardingBundle = surveyBundleRepository.save(new SurveyBundle());
        OnboardingSurvey onboardingSurvey =
            surveyRepository.save(new OnboardingSurvey("온보딩질문내용1", onboardingBundle));
        characterRecordRepository.save(
            CharacterRecord.builder()
                .valueCharacter(valueCharacter)
                .ordinalNumber(1)
                .characterNo("첫번째 캐릭터")
                .member(member)
                .surveyBundle(onboardingBundle)
                .build());

        final SurveyBundle surveyBundle = new SurveyBundle();
        surveyBundleRepository.save(surveyBundle);
        BalanceSurvey balanceSurvey1 =
            surveyRepository.save(new BalanceSurvey("질문내용1", surveyBundle));
        BalanceSurvey balanceSurvey2 =
            surveyRepository.save(new BalanceSurvey("질문내용2", surveyBundle));
        List<KeywordScore> scores =
            List.of(
                KeywordScore.builder().keyword(Keyword.ADVENTURE).score(BigDecimal.ONE).build(),
                KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());
        SurveyOption option1 =
            surveyOptionRepository.save(
                SurveyOption.builder()
                    .survey(balanceSurvey1)
                    .scores(scores)
                    .content("질문 옵션 내용1")
                    .build());
        SurveyOption option2 =
            surveyOptionRepository.save(
                SurveyOption.builder()
                    .survey(balanceSurvey2)
                    .scores(scores)
                    .content("질문 옵션 내용2")
                    .build());

        SurveySubmissionCommand command =
            new SurveySubmissionCommand(
                option1.getId(), balanceSurvey1.getId(), member.getId(), "나는 행복한게 좋으니까");

        // when
        surveyService.submitSurvey(command);
        // then
        List<SurveySubmission> submissions = surveySubmissionRepository.findAll();
        then(submissions).hasSize(1);
        then(submissions.getFirst())
            .extracting("member.id", "survey.id", "selectedOption.id", "retrospective")
            .containsExactly(
                member.getId(), balanceSurvey1.getId(), option1.getId(), "나는 행복한게 좋으니까");
        then(characterRecordRepository.findAll())
            .hasSize(2)
            .extracting(
                "ordinalNumber", "characterNo", "member.id", "surveyBundle.id", "valueCharacter")
            .containsExactly(
                tuple(1, "첫번째 캐릭터", member.getId(), onboardingBundle.getId(), valueCharacter),
                tuple(2, "두번째 캐릭터", member.getId(), surveyBundle.getId(), null));
      }
    }

    @DisplayName("설문이 존재하지 않으면")
    @Nested
    class whenSurveyNotFound {

      @Test
      @DisplayName("예외를 발생시킨다")
      void throwSurveyNotFoundException() {
        // given
        Member member = Member.create("나민혁", "test@test.com");
        memberRepository.save(member);
        SurveyBundle surveyBundle = new SurveyBundle();
        surveyBundleRepository.save(surveyBundle);
        BalanceSurvey balanceSurvey = new BalanceSurvey("질문내용", surveyBundle);
        surveyRepository.save(balanceSurvey);
        List<KeywordScore> scores =
            List.of(
                KeywordScore.builder().keyword(Keyword.ADVENTURE).score(BigDecimal.ONE).build(),
                KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());
        SurveyOption option =
            SurveyOption.builder().survey(balanceSurvey).scores(scores).content("질문 옵션 내용").build();
        surveyOptionRepository.save(option);

        SurveySubmissionCommand command =
            new SurveySubmissionCommand(0L, member.getId(), option.getId(), "나는 행복한게 좋으니까");

        // when
        // then
        thenThrownBy(() -> surveyService.submitSurvey(command))
            .isEqualTo(CustomException.SURVEY_NOT_FOUND);
      }
    }

    @DisplayName("회원과 설문을 찾았고")
    @Nested
    class whenMemberAndSurveyFound {

      @DisplayName("설문 번들의 모든 설문 응답을 제출하였다면")
      @Nested
      class whenSurveyBundleIsCompleted {

        @DisplayName("캐릭터를 생성한다.")
        @Test
        void shouldCreateCharacter() {
          final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
          final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());

          final BalanceSurvey survey =
              surveyRepository.save(
                  new BalanceSurvey(
                      "꿈에 그리던 드림 기업에 입사했다. 연봉도 좋지만, 무엇보다 회사의 근무 방식이 나와 잘 맞는 것 같다. 우리 회사의 근무 방식은...",
                      bundle));
          final SurveyOption option =
              surveyOptionRepository.save(
                  SurveyOption.builder()
                      .survey(survey)
                      .content("자율 출퇴근제로 원하는 시간에 근무하며 창의적인 성과 내기")
                      .scores(
                          List.of(
                              KeywordScore.builder()
                                  .keyword(SELF_DIRECTION)
                                  .score(BigDecimal.ONE)
                                  .build()))
                      .build());

          // 가치관 캐릭터
          final ValueCharacter valueCharacter =
              valueCharacterRepository.save(
                  ValueCharacter.builder()
                      .name("마이웨이 유형")
                      .description("마이웨이 유형 설명")
                      .keyword(SELF_DIRECTION)
                      .build());
          // 온보딩 캐릭터
          final SurveyBundle onboardingBundle = surveyBundleRepository.save(new SurveyBundle());
          characterRecordRepository.save(
              CharacterRecord.builder()
                  .ordinalNumber(1)
                  .characterNo("첫번째 캐릭터")
                  .valueCharacter(valueCharacter)
                  .member(member)
                  .surveyBundle(onboardingBundle)
                  .build());

          final SurveySubmissionCommand command =
              new SurveySubmissionCommand(
                  option.getId(),
                  survey.getId(),
                  member.getId(),
                  "자유롭게 일하면 집중이 잘되는 시간에 일할 수 있기 때문에 일의 능률이 오를 것 같다.");

          surveyService.submitSurvey(command);
          then(characterRecordRepository.findAll())
              .hasSize(2)
              .extracting(
                  "ordinalNumber",
                  "characterNo",
                  "member.id",
                  "surveyBundle.id",
                  "valueCharacter.id")
              .containsExactly(
                  tuple(
                      1,
                      "첫번째 캐릭터",
                      member.getId(),
                      onboardingBundle.getId(),
                      valueCharacter.getId()),
                  tuple(2, "두번째 캐릭터", member.getId(), bundle.getId(), valueCharacter.getId()));
        }
      }

      @DisplayName("설문 번들의 설문 응답이 남아있다면")
      @Nested
      class whenSurveyBundleIsInProgress {

        @DisplayName("캐릭터를 생성하지 않는다.")
        @Test
        void shouldNotCreateCharacter() {
          final Member member = memberRepository.save(Member.create("홍길동", "test@example.com"));
          final SurveyBundle bundle = surveyBundleRepository.save(new SurveyBundle());

          final BalanceSurvey survey1 =
              surveyRepository.save(
                  new BalanceSurvey(
                      "꿈에 그리던 드림 기업에 입사했다. 연봉도 좋지만, 무엇보다 회사의 근무 방식이 나와 잘 맞는 것 같다. 우리 회사의 근무 방식은...",
                      bundle));
          final BalanceSurvey survey2 =
              surveyRepository.save(
                  new BalanceSurvey("독립에 대한 고민이 깊어지는 요즘... 드디어 결정을 내렸다.", bundle));
          final BalanceSurvey survey3 = surveyRepository.save(new BalanceSurvey("세번째 질문", bundle));
          final SurveyOption option1 =
              surveyOptionRepository.save(
                  SurveyOption.builder()
                      .survey(survey1)
                      .content("자율 출퇴근제로 원하는 시간에 근무하며 창의적인 성과 내기")
                      .scores(
                          List.of(
                              KeywordScore.builder()
                                  .keyword(SELF_DIRECTION)
                                  .score(BigDecimal.ONE)
                                  .build()))
                      .build());
          final SurveyOption option2 =
              surveyOptionRepository.save(
                  SurveyOption.builder()
                      .survey(survey2)
                      .content("내 취향대로 꾸민 집에서 자유롭게 생활하기")
                      .scores(
                          List.of(
                              KeywordScore.builder()
                                  .keyword(SELF_DIRECTION)
                                  .score(BigDecimal.ONE)
                                  .build()))
                      .build());
          final SurveyOption option3 =
              surveyOptionRepository.save(
                  SurveyOption.builder()
                      .survey(survey3)
                      .content("세번째 답변")
                      .scores(
                          List.of(
                              KeywordScore.builder()
                                  .keyword(SELF_DIRECTION)
                                  .score(BigDecimal.ONE)
                                  .build()))
                      .build());

          surveySubmissionRepository.save(
              SurveySubmission.builder()
                  .survey(survey1)
                  .member(member)
                  .selectedOption(option1)
                  .submittedAt(LocalDateTime.of(2025, 2, 17, 0, 0, 0))
                  .build());

          final SurveySubmissionCommand command =
              new SurveySubmissionCommand(
                  option2.getId(),
                  survey2.getId(),
                  member.getId(),
                  "자유롭게 일하면 집중이 잘되는 시간에 일할 수 있기 때문에 일의 능률이 오를 것 같다.");

          surveyService.submitSurvey(command);
          then(characterRecordRepository.findAll()).hasSize(0);
        }
      }
    }
  }

  @Test
  void 마지막_제출이_온보딩일_경우_당일이어도_질문에_대한_응답이_가능하다() {
    // given
    Member member = Member.create("나민혁", "test@test.com");
    memberRepository.save(member);
    SurveyBundle surveyBundle1 = new SurveyBundle();
    SurveyBundle surveyBundle2 = new SurveyBundle();

    surveyBundleRepository.saveAll(List.of(surveyBundle1, surveyBundle2));

    OnboardingSurvey survey1 = new OnboardingSurvey("대학 동기 모임에서 나의 승진 이야기가 나왔습니다", surveyBundle1);
    OnboardingSurvey survey2 = new OnboardingSurvey("회사에서 팀 리더로 뽑혔습니다", surveyBundle1);
    OnboardingSurvey survey3 = new OnboardingSurvey("나의 행복 지수는", surveyBundle1);
    OnboardingSurvey survey4 = new OnboardingSurvey("나는 노는게 좋다.", surveyBundle1);

    surveyRepository.saveAll(List.of(survey1, survey2, survey3, survey4));

    List<KeywordScore> scores =
        List.of(
            KeywordScore.builder().keyword(Keyword.ADVENTURE).score(BigDecimal.ONE).build(),
            KeywordScore.builder().keyword(Keyword.BENEVOLENCE).score(BigDecimal.TWO).build());

    SurveyOption option1 =
        SurveyOption.builder().survey(survey1).scores(scores).content("한다.").build();
    SurveyOption option2 =
        SurveyOption.builder().survey(survey2).scores(scores).content("한다.").build();
    SurveyOption option3 =
        SurveyOption.builder().survey(survey3).scores(scores).content("3점").build();
    SurveyOption option4 =
        SurveyOption.builder().survey(survey4).scores(scores).content("4점").build();

    surveyOptionRepository.saveAll(List.of(option1, option2, option3, option4));

    SurveySubmission submission1 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option1)
            .survey(survey1)
            .submittedAt(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
            .build();
    SurveySubmission submission2 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option2)
            .survey(survey2)
            .submittedAt(LocalDateTime.of(2025, 1, 2, 12, 0, 0))
            .build();
    SurveySubmission submission3 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option3)
            .survey(survey3)
            .submittedAt(LocalDateTime.of(2025, 1, 3, 12, 0, 0))
            .build();
    SurveySubmission submission4 =
        SurveySubmission.builder()
            .member(member)
            .selectedOption(option4)
            .survey(survey4)
            .submittedAt(LocalDateTime.of(2025, 1, 4, 12, 0, 0))
            .build();

    surveySubmissionRepository.saveAll(List.of(submission1, submission2, submission3, submission4));

    // when
    SurveyHistoryResponse response = surveyService.getSurveyHistory(member.getId());
    // then
    then(response)
        .extracting("bundleId", "nextSurveyIndex", "isCompleted")
        .containsExactly(surveyBundle2.getId(), 1, false);
  }
}
