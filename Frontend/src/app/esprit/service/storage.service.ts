import { Token } from "@angular/compiler";
import {Injectable} from "@angular/core";

const USER_KEY = 'auth-user';

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  private accessTokenKey = 'accessToken';
  private refreshTokenKey = 'refreshToken';
  private userProfile: any;
  private token: string;
  private readonly LOGIN_TYPE_KEY = 'login-type';


  constructor() {}
  
  setLoginType(loginType: string): void {
    sessionStorage.setItem(this.LOGIN_TYPE_KEY, loginType);
  }

  getLoginType(): string {
    return sessionStorage.getItem(this.LOGIN_TYPE_KEY) || '';
  }

  setUserProfile(profile: any, token: string): void {
    this.userProfile = profile;
    this.token = token;
    // Optionally, you can store these in localStorage/sessionStorage
    sessionStorage.setItem('userProfile', JSON.stringify(profile));
    sessionStorage.setItem('token', token);
  }

  getUserProfile(): any {
    return this.userProfile || JSON.parse(sessionStorage.getItem('userProfile') || '{}');
  }

  getToken(): string {
    return this.token || sessionStorage.getItem('token');
  }

  setTokens(acessToken: string, refreshToken: string): void {
    sessionStorage.setItem(this.accessTokenKey, acessToken);
    sessionStorage.setItem(this.refreshTokenKey, refreshToken);
  }

  getUserId(): number | null {
    const userId = sessionStorage.getItem('id');
    return userId ? parseInt(userId, 10) : null;
  }
  

  getAccessToken(): string | null {
    return sessionStorage.getItem(this.accessTokenKey);
  }

  getRefreshToken(): string | null {
    return sessionStorage.getItem(this.refreshTokenKey);
  }

  clearTokens(): void {
    sessionStorage.removeItem(this.accessTokenKey);
    sessionStorage.removeItem(this.refreshTokenKey);
  }
  
  clean(): void {
    window.sessionStorage.clear();
  }

  public signOut(): void {
    window.sessionStorage.removeItem(USER_KEY);
    // Optionally, you can clear other session-related data or perform additional cleanup
  }

  public saveUser(user: any): void {
    // Save only necessary user data, like username and role
    const userData = {
      id : user.id,
      imageTest : user.image,
      username: user.username, // Assuming this is where your access token is stored
      role: user.role,
      Token : user.acessToken,
      activated : user.activated
    };

    window.sessionStorage.removeItem(USER_KEY); // Remove any existing user data
    window.sessionStorage.setItem(USER_KEY, JSON.stringify(userData)); // Store new user data
  }

  public getUser(): any {
    const user = window.sessionStorage.getItem(USER_KEY);
    if (user) {
      return JSON.parse(user); // Parse and return the user object
    }
  
    return null; // Return null if no user data found
  }

  getIdUser(): number | null {
    const userData = sessionStorage.getItem(USER_KEY);
    if (userData) {
      const user = JSON.parse(userData);
      return user.id ? parseInt(user.id, 10) : null;
    }
    return null;
  }
  

isLoggedIn(): boolean {
  const userData = sessionStorage.getItem(USER_KEY);
  console.log("Données utilisateur dans sessionStorage:", userData);
  return !!userData; // Retourne true si des données utilisateur existent, false sinon
}
 
}
