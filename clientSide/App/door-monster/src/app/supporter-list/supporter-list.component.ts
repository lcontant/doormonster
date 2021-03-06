import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-supporter-list',
  templateUrl: './supporter-list.component.html',
  styleUrls: ['./supporter-list.component.css']
})
export class SupporterListComponent implements OnInit {

  supportList: string[]  = [
    "killersoda57",
    "DS_Drennen",
    "ThatGuy8999",
    "Collective",
    "Isaac Steele",
    "L. Lavigne",
    "Merlin",
    "insomnite",
    "Billy Berg",
    "MeatWizard",
    "themagicrabbit",
    "Kevin Machate",
    "Jake Golun",
    "BrothrBear",
    "Raptor9",
    "Gentleman Wolfgang",
    "InvictusByzantium",
    "Foxworth",
    "Rebecca Wortham",
    "Silverwing_Actual",
    "Crowborn Chaos",
    "Edward Jeavons",
    "Jeffrey Pohl",
    "David Iacobbo",
    "Benoît Labrecque",
    "Josiah Murray",
    "EWanderer",
    "Ian Gardiner",
    "Robert pulbrook",
    "Robert Shaw",
    "Michael Bell",
    "Neel Taylor",
    "Dillon Dishman",
    "Jordan Kubicki",
    "richard r14",
    "Justin Dickerson",
    "Nathan Chaney",
    "b h",
    "Xandar Miller",
    "Daniel Fox - https://tinyurl.com/y29zwhng",
    "Josh Jackson",
    "NekoMercutio",
    "Trey Nix",
    "John Cornelius",
    "Carson Elmore",
    "Julian Arnott",
    "Rhett McPherson",
    "Kian Sobhanpanah",
    "Daniel McCormack",
    "Jacob Reddy",
    "David Reddick",
    "Corey Skuse",
    "Karisa Elizabeth Castro",
    "Kevin Bärudde",
    "Dichu",
    "Ville Valste",
    "Omenæ",
    "NoInfoGiven",
    "Daniel Foerster",
    "The Nferno",
    "Sketchbeard",
    "Michael Hlavavty",
    "Felix Caesar",
    "Ghost8472",
    "Hades Himself",
    "Joseph L. Selby",
    "Connor Wiegand",
    "Eric da' MAJ",
    "Xeno Martinez",
    "MadKing",
    "Hithroc Mehatoko",
    "Kore",
    "Grant Parks",
    "Radiogenic",
    "Taran Perman",
    "Christian Johnson - https://goo.gl/Ln5RkL",
    "Matthew H. Keefe",
    "akn318",
    "Robert Stegmann",
    "Fabian Voß",
    "AlwildatheKind",
    "Alex Staben",
    "Stuart Hamm",
    "The_Imago",
    "Kane Dimitroff - https://www.twitch.tv/dimi_god11",
    "soltytron",
    "Cyrus McCleery",
    "Encreedem",
    "The Silver Warrior",
    "Vak Tri'tor",
    "Salfur",
    "Dahvien Dean",
    "Sam Steup",
    "Thomas Pike",
    "David Kaufman",
    "kyeen",
    "Nate Scheper",
    "BlackOpsElf",
    "Erin Cooke",
    "Daniel Vargas",
    "Jralg Ph4d3r",
    "Damian Cutler",
    "Felix Dryba",
    "Ry Muzz",
    "Alexander Birt",
    "William McCreight",
    "Grace Voege",
    "Noah E C Klowden",
    "Madeline Dunsmore",
    "Daniel Hiatt",
    "Teresa Gaskins",
    "Craig Bester",
    "Eric T Lunde",
    "Michael McEwan",
    "Maxwell Boehm",
    "alani santamarina",
    "Tevan Cota",
    "Samuel Bynum",
    "Rob",
    "Drew Meltzer",
    "DFStarfield",
    "Johnpaul Morrow",
    "Matthew",
    "Zachary Helfrich",
    "eractnodi",
    "Ian Hazleton - http://frogsfolly.com/",
    "Nathan Landrum",
    "Kain",
    "Mobiix",
    "Zach Newman",
    "Paul Ngo",
    "Dean A Carter",
    "Marcus Macaulay",
    "andrew lawson",
    "drezirale .",
    "Thomas Marsh",
    "Anthony LaCroix",
    "Peter Stadtmueller",
    "Jeroen Plug",
    "??????? ???",
    "Yoshiaki Joel Koga",
    "Joseph Comparette",
    "Him",
    "Canadianator",
    "Thomas Plotz",
    "Samuel Riggs",
    "Yamin4-Studios",
    "Nathan Soller",
    "Yorie1234",
    "Cordell Casias",
    "Gregory Makar",
    "The_True_Phoenix_King",
    "Luke Miller",
    "Liam Bewley",
    "Zaigan Redman",
    "mind vs. real vs. game",
    "Allen Peele",
    "Lucas Davis-Butters",
    "Jadon D Dester",
    "Rorksen",
    "Per Danborg",
    "Adrian Noland",
    "Starlighter",
    "Gaylon Taylor",
    "Daniel Brazell",
    "John Condakes",
    "erroxes",
    "Ex Caseus Fortis",
    "Stephen",
    "Marco Zimmer",
    "T. I. Masterson",
    "Marco Meza",
    "Doc Mnc",
    "Joshua Stevens",
    "Millicent Davis",
    "Sarah Stillman",
    "Richard Karlsson",
    "Sam Triplett",
    "Jakob Lundin",
    "Vengarth",
    "ins0mn",
    "Keengunman",
    "Hayden Thompson",
    "Wutata",
    "Anne-Roos Strik",
    "Not_Walter",
    "Stijn Verwijmeren",
    "AJ Covert - https://tinyurl.com/y68cqg9o",
    "Sandy Suh",
    "Liam Russell",
    "Delasee",
    "mathew wilken",
    "Jeremy Viar",
    "Adam",
    "Shadowfacts",
    "\"Reacted\"",
    "Ryan Toporcer",
    "Sno Comics",
    "Bryce",
    "Liam Hay",
    "Hermes Higashino",
    "Isaac James-Grover Twardowski",
    "Jesse Frank",
    "Nimrod Rappaport",
    "Matthew Frey",
    "Sauyon Lee",
    "Emrys Rose",
    "Tim Glennon",
    "Wildmanden",
    "Ethan Gould",
    "Achim",
    "Bradley Mitchell",
    "Jack Honey",
    "Natan Iliaz Zekic",
    "Deiren",
    "KvaGram",
    "Tomer David",
    "Drew Harris",
    "Jonathon Meeks",
    "Barak Austin",
    "Michael-Erik",
    "Nico Feiertag",
    "James Reeve",
    "Cornelis Clüver",
    "Anton Trees",
    "Robin-Michael Becker",
    "Alexander Olsen",
    "SteelStarling",
    "powercore2000",
    "Andrew Newman",
    "Jeremy Sherman - https://tinyurl.com/y68bz4e8",
    "Aidan Prentice",
    "Travis Ridge",
    "Gabriel Poprave",
    "Cordell Finnson",
    "Joshua Matson",
    "Taringator",
    "Liam Visman",
    "McLean Mullen",
    "Dinonumber",
    "Nicholas McDonald",
    "Sean Eyre",
    "ARTQ",
    "Jesse",
    "Luke Dunham",
    "Joshua Kho",
    "Kyle Napolitano",
    "Daniel Page",
    "HowToZombie",
    "Britteny Smith",
    "Lawrence Gray",
    "Mitchell Avallone",
    "David Sangrey",
    "Doc Janowiak",
    "Blue Dragon",
    "Andrew Whitcomb",
    "William",
    "Emiel Magnée",
    "Riley Gibson",
    "Malachi Hinkle",
    "benjamin chelsky",
    "Cory Fox",
    "Trip Might",
    "Makkovar - https://www.twitch.tv/makkovar",
    "Jackson Turner",
    "Eric Dusseau",
    "Alastair Cranston",
    "Kenan Millet",
    "Cody Hendriks",
    "Micaela Stewart",
    "Benjamin Daily",
    "Benjamin Wells",
    "Alan Kraut",
    "Khaim",
    "Angus",
    "Joel Shaffer",
    "Wandering Whigmaleerie - https://www.wandering-whims.com/",
    "Captain Tyrfingr",
    "Nicholas Logan",
    "Andrew Sprague",
    "Emily Ramos",
    "Jonathan Zhuang",
    "Tobias Ormås",
    "Jared Rose",
    "Admiral Hunter",
    "Heath Skinner",
    "Eric Korbage",
    "Patrick Conell",
    "Ghizghuth",
    "Ryan Marc Bridie",
    "Crimsonite3",
    "Thomas Hall",
    "zachary Alan Dozier",
    "Captain Big Mac",
    "Strategia in Ultima",
    "Kathryn Snead",
    "sensenmennel",
    "BD",
    "xerlyn",
    "Kyle Middleton",
    "Robert Green",
    "Crada",
    "Alexander McFall",
    "Guy Teichman",
    "Zoë Maxwell",
    "Tofuaner",
    "Dan Walker",
    "Tylen",
    "Edward Petersen",
    "Michael Barnes",
    "Andrew Durand",
    "Wilkans",
    "Matt Kiernan",
    "Light of the Blue Night",
    "Xavier Kienast",
    "Adrian Money",
    "Buddahjonesey streams",
    "Admiral bard",
    "TacticalFerret"];
  constructor() {
  }

  ngOnInit() {
    let regex = /(.*) - (.*)/g;
    for (let i = 0; i < this.supportList.length; i++) {
      let matches = regex.exec(this.supportList[i]);
      if (matches != null) {
        this.supportList[i] = `<a href="${matches[2]}">${matches[1]}</a>`;
      }
    }
  }

}
