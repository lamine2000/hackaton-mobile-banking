import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { IQueryParams, createEntitySlice, EntityState, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IInsuranceAndMicroCreditsContribution, defaultValue } from 'app/shared/model/insurance-and-micro-credits-contribution.model';

const initialState: EntityState<IInsuranceAndMicroCreditsContribution> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrl = 'api/insurance-and-micro-credits-contributions';
const apiSearchUrl = 'api/_search/insurance-and-micro-credits-contributions';

// Actions

export const searchEntities = createAsyncThunk(
  'insuranceAndMicroCreditsContribution/search_entity',
  async ({ query, page, size, sort }: IQueryParams) => {
    const requestUrl = `${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`;
    return axios.get<IInsuranceAndMicroCreditsContribution[]>(requestUrl);
  }
);

export const getEntities = createAsyncThunk(
  'insuranceAndMicroCreditsContribution/fetch_entity_list',
  async ({ page, size, sort }: IQueryParams) => {
    const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}&` : '?'}cacheBuster=${new Date().getTime()}`;
    return axios.get<IInsuranceAndMicroCreditsContribution[]>(requestUrl);
  }
);

export const getEntity = createAsyncThunk(
  'insuranceAndMicroCreditsContribution/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<IInsuranceAndMicroCreditsContribution>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const createEntity = createAsyncThunk(
  'insuranceAndMicroCreditsContribution/create_entity',
  async (entity: IInsuranceAndMicroCreditsContribution, thunkAPI) => {
    const result = await axios.post<IInsuranceAndMicroCreditsContribution>(apiUrl, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const updateEntity = createAsyncThunk(
  'insuranceAndMicroCreditsContribution/update_entity',
  async (entity: IInsuranceAndMicroCreditsContribution, thunkAPI) => {
    const result = await axios.put<IInsuranceAndMicroCreditsContribution>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const partialUpdateEntity = createAsyncThunk(
  'insuranceAndMicroCreditsContribution/partial_update_entity',
  async (entity: IInsuranceAndMicroCreditsContribution, thunkAPI) => {
    const result = await axios.patch<IInsuranceAndMicroCreditsContribution>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const deleteEntity = createAsyncThunk(
  'insuranceAndMicroCreditsContribution/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await axios.delete<IInsuranceAndMicroCreditsContribution>(requestUrl);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

// slice

export const InsuranceAndMicroCreditsContributionSlice = createEntitySlice({
  name: 'insuranceAndMicroCreditsContribution',
  initialState,
  extraReducers(builder) {
    builder
      .addCase(getEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      .addMatcher(isFulfilled(getEntities, searchEntities), (state, action) => {
        const { data, headers } = action.payload;

        return {
          ...state,
          loading: false,
          entities: data,
          totalItems: parseInt(headers['x-total-count'], 10),
        };
      })
      .addMatcher(isFulfilled(createEntity, updateEntity, partialUpdateEntity), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      })
      .addMatcher(isPending(getEntities, getEntity, searchEntities), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isPending(createEntity, updateEntity, partialUpdateEntity, deleteEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = InsuranceAndMicroCreditsContributionSlice.actions;

// Reducer
export default InsuranceAndMicroCreditsContributionSlice.reducer;
