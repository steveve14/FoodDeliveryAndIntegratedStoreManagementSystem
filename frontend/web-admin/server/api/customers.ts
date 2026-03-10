import { fetchBackendData } from '../utils/backendApi';

export default eventHandler(async (event) => {
  return fetchBackendData(event, '/api/v1/users/frontend/customers');
});
