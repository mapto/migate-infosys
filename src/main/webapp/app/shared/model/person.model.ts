import dayjs from 'dayjs';
import { IRoleInstitution } from 'app/shared/model/role-institution.model';
import { IRoleWork } from 'app/shared/model/role-work.model';
import { IWork } from 'app/shared/model/work.model';
import { Gender } from 'app/shared/model/enumerations/gender.model';
import { Country } from 'app/shared/model/enumerations/country.model';
import { Language } from 'app/shared/model/enumerations/language.model';

export interface IPerson {
  id?: number;
  name?: string;
  gender?: Gender | null;
  dob?: string | null;
  dod?: string | null;
  country?: Country | null;
  language?: Language | null;
  jobs?: IRoleInstitution[] | null;
  responsibilities?: IRoleWork[] | null;
  works?: IWork[] | null;
}

export const defaultValue: Readonly<IPerson> = {};
