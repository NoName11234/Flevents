import api from "@/api/api";
import {FleventsEvent} from "@/models/fleventsEvent";
import {EventRole} from "@/models/eventRole";
import {Post} from "@/models/post";
import {Questionnaire} from "@/models/questionnaire";
import {AnsweredQuestionnaire} from "@/models/answeredQuestionnaire";

/**
 * Api endpoints to access and modify posts.
 * @author David Maier
 * @since Weekly Build 3
 */

class QuestionnaireApi {

  // CRUD

  /**
   * Creates a Questionnaire.
   * @param questionnaire the questionnaire to be created
   * @param eventUuid the uuid of the event the post belongs to
   */
  create(questionnaire: Questionnaire, eventUuid: string) {
    return api.post(`/questionnaires`, questionnaire, {
      headers: { "Content-Type": undefined },
      params: { "eventId": eventUuid}
    });
  }

  /**
   * Retrieves a specific questionnaire.
   * @param uuid the uuid of the questionnaire
   */
  get(uuid: string) {
    return api.get(`/questionnaires/${uuid}`);
  }

  /**
   * Retrieves all questionnaires of the given event.
   * @param eventUuid the uuid of the event
   */
  getOf(eventUuid: string) {
    return api.get("/questionnaires", {params:{ eventId: eventUuid}});
  }

  /**
   * Retrieves the Answer of the questionnaire.
   * @param questionnaireUuid the uuid of the questionnaire
   * @param userUuid the Uuid of the user
   */
  getAnswers(questionnaireUuid : string, userUuid: string) {
    return api.get(`/questionnaires/${questionnaireUuid}/answers/${userUuid}`);
  }

  /**
   * saves the Answer of the questionnaire.
   * @param answeredQuestionnaire the answered questionnaire
   * @param questionnaireId the Uuid of the questionnaire
   */
  saveAnswer(answeredQuestionnaire : AnsweredQuestionnaire, questionnaireId : string){
    return api.post(`/questionnaires/${questionnaireId}/answers`, answeredQuestionnaire)
  }

  /**
   * Deletes a questionnaire.
   * @param uuid the uuid of the questionnaire
   */
  delete(uuid: string) {
    return api.delete(`/questionnaires/${uuid}`);
  }

  /**
   * Retrieves statistics about the questionnaire.
   * @param questionnaireUuid the uuid of the questionnaire
   */
  getStatistics(questionnaireUuid : string) {
    return api.get(`/questionnaires/${questionnaireUuid}/statistics`);
  }
}

export default new QuestionnaireApi();
