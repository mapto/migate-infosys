import { IInstitution } from 'app/shared/model/institution.model';
import { Country } from 'app/shared/model/enumerations/country.model';

export interface ILocation {
  id?: number;
  address?: string;
  country?: Country | null;
  owners?: IInstitution[] | null;
}

export const defaultValue: Readonly<ILocation> = {};
