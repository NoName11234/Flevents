import {SingleChoiceQuestionSummary} from "@/models/singleChoiceQuestionSummary";
import {SingleChoiceQuestion} from "@/models/singleChoiceQuestion";
import {COLORS} from "@/constants";
import {FreeTextQuestionSummary} from "@/models/freeTextQuestionSummary";

class StatisticsService {

  /**
   * Converts a single choice question and its summary to a diagram data object.
   * @param question the single choice question
   * @param summary the question's summary
   */
  toDiagramData(question: SingleChoiceQuestion, summary: SingleChoiceQuestionSummary) {
    return {
      labels: question.choices.map(c => c.choice),
      datasets: [
        {
          label: question.question,
          data: summary.votes,
          backgroundColor: COLORS.CHART_COLORS,
        }
      ]
    } as any;
  }

  /**
   * Unifies answers of a free text question to eliminate duplicates and count them.
   * @param summary the free text question's summary
   */
  unifyTextResults(summary: FreeTextQuestionSummary) {
    let counts = new Map<string, number>;
    let currentAnswer = '';
    let currentCount = 0;
    for (let answer of summary.answers.sort()) {
      if (answer === currentAnswer) {
        currentCount++;
        continue;
      }
      if (currentCount > 0) {
        counts.set(currentAnswer, currentCount);
      }
      currentAnswer = answer;
      currentCount = 1;
    }
    if (currentCount > 0) {
      counts.set(currentAnswer, currentCount);
    }
    return counts;
  }

}

/**
 * Util-class for handling survey results.
 * @author David Maier
 * @since Weekly Build 4
 */
export default new StatisticsService();
