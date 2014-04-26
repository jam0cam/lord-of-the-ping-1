//
//  LeaderboardItem.m
//  Lord of the Ping
//
//  Created by Matthew Runo on 4/25/14.
//  Copyright (c) 2014 Lord of the Ping. All rights reserved.
//

#import "LeaderboardItem.h"

@implementation LeaderboardItem
@synthesize player;
@synthesize matchWin;
@synthesize matchLosses;
@synthesize winningPercentage;

-(void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.player forKey:@"player"];
    [coder encodeInt:self.matchWin forKey:@"matchWin"];
    [coder encodeFloat:self.matchLosses forKey:@"matchLosses"];
    [coder encodeFloat:self.winningPercentage forKey:@"winningPercentage"];
}

- (id)initWithCoder:(NSCoder *) coder {
    self = [super init];
    if( self ){
        player = [coder decodeObjectForKey:@"player"];
        matchWin = [coder decodeIntForKey:@"matchWin"];
        matchLosses = [coder decodeFloatForKey:@"matchLosses"];
        winningPercentage = [coder decodeFloatForKey:@"winningPercentage"];
    }
    
    return self;
}

@end
