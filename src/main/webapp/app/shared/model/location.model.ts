import { IInstitution } from 'app/shared/model/institution.model';
import { ICountry } from 'app/shared/model/country.model';

export interface ILocation {
  id?: number;
  address?: string | null;
  sites?: IInstitution | null;
  places?: ICountry | null;
}

export const defaultValue: Readonly<ILocation> = {};
