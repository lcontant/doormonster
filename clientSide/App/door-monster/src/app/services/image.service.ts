import { Injectable } from '@angular/core';
// @ts-ignore
var b64toBlob = require('b64-to-blob');

@Injectable({
  providedIn: 'root'
})
export class ImageService {
  constructor() {

  }

  toBlob(uriString: string) {
    return b64toBlob(uriString.split(",")[1]);
  }
}
