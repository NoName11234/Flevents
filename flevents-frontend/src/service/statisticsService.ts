import {ChartData} from "chart.js";
import {SingleChoiceQuestionSummary} from "@/models/singleChoiceQuestionSummary";
import {SingleChoiceQuestion} from "@/models/singleChoiceQuestion";
import {useTheme} from "vuetify";

class StatisticsService {

  toDiagramData(question: SingleChoiceQuestion, summary: SingleChoiceQuestionSummary): ChartData {
    const theme = useTheme();
    return {
      labels: question.choices.map(c => c.choice),
      datasets: [
        {
          data: summary.votes,
          backgroundColor: theme.current.value.colors.secondary,
        }
      ]
    } as ChartData;
  }

}

/**
 * Util-class for handling survey results.
 * @author David Maier
 * @since Weekly Build 4
 */
export default new StatisticsService();
