import { ILocation } from 'app/shared/model/location.model';
import { IPerson } from 'app/shared/model/person.model';

export interface ICountry {
  id?: number;
  locations?: ILocation[] | null;
  person?: IPerson | null;
}

export const defaultValue: Readonly<ICountry> = {};
