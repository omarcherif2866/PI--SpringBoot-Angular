import { Session } from "./session";
import { User } from "./user";

export interface Activite {
    id?:number;
    nom?:string;
    description?:string;
    annimateur?:Annimateur;
    image?:string;
    listSessions?: number[]; 
    reservationIds?: number[]; 
    users?:User[];
    nbrParticipants?:number


}

export enum Annimateur {
    ANIMATEUR_1 = "Equipe_de_coachs",
    ANIMATEUR_2 = "Esprit",
    ANIMATEUR_3 = "Gouvernorat_de_tunis",
    ANIMATEUR_4 = "Coach_Souha",

}