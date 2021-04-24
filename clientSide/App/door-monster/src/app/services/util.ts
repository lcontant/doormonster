export class Util {

  public static removeSpecialStrings(rawString : string) {
      return rawString.replace("&amp;","&");
  }
}
