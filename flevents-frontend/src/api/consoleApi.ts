import api from "@/api/api";
import {FleventsEvent} from "@/models/fleventsEvent";
import {EventRole} from "@/models/eventRole";
import {Post} from "@/models/post";
import {Organization} from "@/models/organization";

/**
 * Api endpoints to access and modify in the administrative console.
 * @author David Maier
 * @since Weekly Build 3
 */

class PostsApi {

  getAllOrganizations() {
    return api.get(`/organizations`);
  }

  createOrganization(organization: Organization, emailOfFirstAdmin: string) {
    return api.post(`/platform/organizations`, organization, {
      params: {
        email: emailOfFirstAdmin,
      }
    });
  }

  deleteOrganization(uuid: string) {
    return api.delete(`/platform/organizations/${uuid}`);
  }

}

export default new PostsApi();
