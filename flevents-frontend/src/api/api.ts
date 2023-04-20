import axios from "axios";


const properties = {
  // url: 'http://h3005487.stratoserver.net:8082/api',
  url: 'http://localhost:8082/api',
}

const api = axios.create({
  baseURL: properties.url,
  // withCredentials: true,
});

export default api;
