//
//  LeaderboardItem.h
//  Lord of the Ping
//
//  Created by Matthew Runo on 4/25/14.
//  Copyright (c) 2014 Lord of the Ping. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Player.h"

@interface LeaderboardItem : NSObject

@property (nonatomic, retain) Player *player;
@property (nonatomic) int *matchWin;
@property (nonatomic) int *matchLosses;
@property (nonatomic) float *winningPercentage;

@end
